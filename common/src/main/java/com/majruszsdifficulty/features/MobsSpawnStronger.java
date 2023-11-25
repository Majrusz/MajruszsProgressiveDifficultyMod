package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.registry.Registries;
import com.majruszlibrary.text.RegexString;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class MobsSpawnStronger {
	private static boolean IS_ENABLED = true;
	private static final AttributeHandler HEALTH = new AttributeHandler( "progressive_difficulty_health_bonus", ()->Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE );
	private static final AttributeHandler DAMAGE = new AttributeHandler( "progressive_difficulty_damage_bonus", ()->Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE );
	private static final GameStageValue< Float > HEALTH_BONUS = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.1f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.2f )
	);
	private static final GameStageValue< Float > DAMAGE_BONUS = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.1f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.2f )
	);
	private static final GameStageValue< Float > NIGHT_MULTIPLIER = GameStageValue.of(
		DefaultMap.defaultEntry( 1.5f )
	);
	private static List< RegexString > EXCLUDED_MOBS = List.of();

	static {
		OnEntitySpawned.listen( MobsSpawnStronger::boost )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->IS_ENABLED )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof Mob mob && DAMAGE.hasAttribute( mob ) )
			.addCondition( data->EXCLUDED_MOBS.stream().noneMatch( id->id.matches( Registries.ENTITY_TYPES.getId( data.entity.getType() ).toString() ) ) );

		Serializables.getStatic( Config.Features.class )
			.define( "mobs_spawn_stronger", MobsSpawnStronger.class );

		Serializables.getStatic( MobsSpawnStronger.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "health_bonus", Reader.map( Reader.number() ), ()->HEALTH_BONUS.get(), v->HEALTH_BONUS.set( v ) )
			.define( "damage_bonus", Reader.map( Reader.number() ), ()->DAMAGE_BONUS.get(), v->DAMAGE_BONUS.set( v ) )
			.define( "night_multiplier", Reader.map( Reader.number() ), ()->NIGHT_MULTIPLIER.get(), v->NIGHT_MULTIPLIER.set( v ) )
			.define( "excluded_mobs", Reader.list( Reader.string() ), ()->RegexString.toString( EXCLUDED_MOBS ), v->EXCLUDED_MOBS = RegexString.toRegex( v ) );
	}

	private static void boost( OnEntitySpawned data ) {
		Mob mob = ( Mob )data.entity;
		GameStage gameStage = GameStageHelper.determineGameStage( data );
		float nightMultiplier = NIGHT_MULTIPLIER.get( gameStage );

		HEALTH.setValue( HEALTH_BONUS.get( gameStage ) * nightMultiplier ).apply( mob );
		DAMAGE.setValue( DAMAGE_BONUS.get( gameStage ) * nightMultiplier ).apply( mob );
		mob.setHealth( mob.getMaxHealth() );
	}
}

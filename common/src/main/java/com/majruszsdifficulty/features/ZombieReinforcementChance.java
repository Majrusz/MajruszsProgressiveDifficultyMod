package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieReinforcementChance {
	private static final AttributeHandler ATTRIBUTE = new AttributeHandler( "progressive_difficulty_reinforcement_chance", ()->Attributes.SPAWN_REINFORCEMENTS_CHANCE, AttributeModifier.Operation.MULTIPLY_BASE );
	private static boolean IS_ENABLED = true;
	private static boolean IS_SCALED_BY_CRD = true;
	private static GameStageValue< Float > CHANCE_BONUS = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.5f ),
		DefaultMap.entry( GameStage.MASTER_ID, 1.0f )
	);

	static {
		OnEntitySpawned.listen( ZombieReinforcementChance::boost )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->IS_ENABLED )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof Zombie );

		Serializables.getStatic( Config.Features.class )
			.define( "zombie_reinforcement_chance", ZombieReinforcementChance.class );

		Serializables.getStatic( ZombieReinforcementChance.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v )
			.define( "extra_chance_multiplier", Reader.map( Reader.number() ), ()->CHANCE_BONUS.get(), v->CHANCE_BONUS = GameStageValue.of( Range.of( 0.0f, 10.0f )
				.clamp( v ) ) );
	}

	private static void boost( OnEntitySpawned data ) {
		float chanceBonus = CHANCE_BONUS.get( GameStageHelper.determineGameStage( data ) );
		if( IS_SCALED_BY_CRD ) {
			chanceBonus *= LevelHelper.getClampedRegionalDifficultyAt( data.getLevel(), data.entity.blockPosition() );
		}

		ATTRIBUTE.setValue( chanceBonus ).apply( ( Zombie )data.entity );
	}
}

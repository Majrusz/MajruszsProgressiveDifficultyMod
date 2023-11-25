package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity {
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.EXPERT_ID );
	private static float DAMAGE_MULTIPLIER = 0.2f;

	static {
		OnEntityPreDamaged.listen( CreeperExplosionImmunity::reduceDamage )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.source.is( DamageTypeTags.IS_EXPLOSION ) );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_explosion_immunity", CreeperExplosionImmunity.class );

		Serializables.getStatic( CreeperExplosionImmunity.class )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "damage_multiplier", Reader.number(), ()->DAMAGE_MULTIPLIER, v->DAMAGE_MULTIPLIER = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}

	private static void reduceDamage( OnEntityPreDamaged data ) {
		data.damage *= DAMAGE_MULTIPLIER;
	}
}

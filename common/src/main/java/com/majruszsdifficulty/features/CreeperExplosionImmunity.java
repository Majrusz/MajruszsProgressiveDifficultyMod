package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszlibrary.contexts.OnEntityPreDamaged;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.disabledOn( GameStage.NORMAL_ID );
	private static float DAMAGE_MULTIPLIER = 0.2f;

	static {
		OnEntityPreDamaged.listen( CreeperExplosionImmunity::reduceDamage )
			.addCondition( data->IS_ENABLED.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.source.is( DamageTypeTags.IS_EXPLOSION ) );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_explosion_immunity", CreeperExplosionImmunity.class );

		Serializables.getStatic( CreeperExplosionImmunity.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "damage_multiplier", Reader.number(), ()->DAMAGE_MULTIPLIER, v->DAMAGE_MULTIPLIER = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}

	private static void reduceDamage( OnEntityPreDamaged data ) {
		data.damage *= DAMAGE_MULTIPLIER;
	}
}

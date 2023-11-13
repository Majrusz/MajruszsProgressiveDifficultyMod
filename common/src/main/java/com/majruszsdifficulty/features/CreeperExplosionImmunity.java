package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.data.Serializables;
import com.mlib.math.Range;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperExplosionImmunity {
	private GameStageValue< Boolean > isEnabled = GameStageValue.disabledOn( GameStage.NORMAL_ID );
	private float damageMultiplier = 0.2f;

	public CreeperExplosionImmunity() {
		OnEntityPreDamaged.listen( this::reduceDamage )
			.addCondition( data->this.isEnabled.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.source.is( DamageTypeTags.IS_EXPLOSION ) );

		Serializables.get( Config.Features.class )
			.define( "creeper_explosion_immunity", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
				subconfig.defineFloat( "damage_multiplier", s->this.damageMultiplier, ( s, v )->this.damageMultiplier = Range.of( 0.0f, 1.0f ).clamp( v ) );
			} );
	}

	private void reduceDamage( OnEntityPreDamaged data ) {
		data.damage *= this.damageMultiplier;
	}
}

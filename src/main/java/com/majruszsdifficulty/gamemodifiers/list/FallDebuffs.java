package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class FallDebuffs extends GameModifier {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( "Nausea", ()->MobEffects.CONFUSION, 0, 8.0 );
	final ProgressiveEffectConfig slowness = new ProgressiveEffectConfig( "Slowness", ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 6.0 );

	public FallDebuffs() {
		super( Registries.Modifiers.DEFAULT, "FallDebuffs", "Inflicts several debuffs when taking fall damage." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this::applyDebuffs );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 1.0, false ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f )
			.addCondition( data->EntityHelper.isHuman( data.target ) )
			.addConfigs( this.nausea, this.slowness );

		this.addContext( onDamaged );
	}

	private void applyDebuffs( OnDamaged.Data data ) {
		this.nausea.apply( data.target );
		this.slowness.apply( data.target );
	}
}

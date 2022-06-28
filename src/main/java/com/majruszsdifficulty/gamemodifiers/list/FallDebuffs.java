package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class FallDebuffs extends GameModifier {
	static final ProgressiveEffectConfig NAUSEA = new ProgressiveEffectConfig( "Nausea", ()->MobEffects.CONFUSION, 0, 8.0 );
	static final ProgressiveEffectConfig SLOWNESS = new ProgressiveEffectConfig( "Slowness", ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 6.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( FallDebuffs::applyDebuffs );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 1.0 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f ) );
		ON_DAMAGED.addConfigs( NAUSEA, SLOWNESS );
	}

	public FallDebuffs() {
		super( GameModifier.DEFAULT, "FallDebuffs", "Inflicts several debuffs when taking fall damage.", ON_DAMAGED );
	}

	private static void applyDebuffs( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		NAUSEA.apply( data.target );
		SLOWNESS.apply( data.target );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class DrownDebuffs extends GameModifier {
	static final ProgressiveEffectConfig NAUSEA = new ProgressiveEffectConfig( "Nausea", ()->MobEffects.CONFUSION, 0, 2.0, 60.0 );
	static final ProgressiveEffectConfig WEAKNESS = new ProgressiveEffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 10.0, 60.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( DrownDebuffs::applyDebuffs );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 1.0 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.equals( DamageSource.DROWN ) ) );
		ON_DAMAGED.addConfigs( NAUSEA, WEAKNESS );
	}

	public DrownDebuffs() {
		super( GameModifier.DEFAULT, "DrownDebuffs", "Inflicts several debuffs when taking drown damage (these debuffs stack).", ON_DAMAGED );
	}

	private static void applyDebuffs( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		NAUSEA.apply( data.target );
		WEAKNESS.apply( data.target );
	}
}

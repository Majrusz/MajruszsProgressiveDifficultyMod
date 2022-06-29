package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class DrownDebuffs extends GameModifier {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( "Nausea", ()->MobEffects.CONFUSION, 0, 2.0, 60.0 );
	final ProgressiveEffectConfig weakness = new ProgressiveEffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 10.0, 60.0 );

	public DrownDebuffs() {
		super( GameModifier.DEFAULT, "DrownDebuffs", "Inflicts several debuffs when taking drown damage (these debuffs stack)." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyDebuffs );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.Chance( 1.0 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.ContextOnDamaged( data->data.source.equals( DamageSource.DROWN ) ) )
			.addConfigs( this.nausea, this.weakness );

		this.addContext( onDamaged );
	}

	private void applyDebuffs( OnDamagedData data ) {
		this.nausea.apply( data.target );
		this.weakness.apply( data.target );
	}
}

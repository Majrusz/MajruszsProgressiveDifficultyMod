package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

@AutoInstance
public class DrownDebuffs extends GameModifier {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( "Nausea", ()->MobEffects.CONFUSION, 0, 2.0, 60.0 );
	final ProgressiveEffectConfig weakness = new ProgressiveEffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 10.0, 60.0 );

	public DrownDebuffs() {
		super( Registries.Modifiers.DEFAULT, "DrownDebuffs", "Inflicts several debuffs when taking drown damage (these debuffs stack)." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this::applyDebuffs );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 1.0, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.source.equals( DamageSource.DROWN ) )
			.addConfigs( this.nausea, this.weakness );

		this.addContext( onDamaged );
	}

	private void applyDebuffs( OnDamaged.Data data ) {
		this.nausea.apply( data.target );
		this.weakness.apply( data.target );
	}
}

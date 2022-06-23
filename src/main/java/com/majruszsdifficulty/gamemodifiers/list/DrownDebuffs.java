package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class DrownDebuffs extends GameModifier {
	static final int MAX_TICKS = Utility.secondsToTicks( 60.0 );
	static final CustomConfigs.ProgressiveEffect NAUSEA = new CustomConfigs.ProgressiveEffect( "Nausea", 0, 2.0 );
	static final CustomConfigs.ProgressiveEffect WEAKNESS = new CustomConfigs.ProgressiveEffect( "Weakness", 0, 10.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 1.0 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.Context<>( OnDamagedContext.Data.class, data->data.source.equals( DamageSource.DROWN ) ) );
		ON_DAMAGED.addConfig( NAUSEA );
		ON_DAMAGED.addConfig( WEAKNESS );
	}

	public DrownDebuffs() {
		super( GameModifier.DEFAULT, "DrownDebuffs", "Inflicts several debuffs when taking drown damage (these debuffs stack).", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.CONFUSION, NAUSEA.getDuration(), NAUSEA.getAmplifier(), MAX_TICKS );
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.WEAKNESS, WEAKNESS.getDuration(), WEAKNESS.getAmplifier(), MAX_TICKS );
		}
	}
}

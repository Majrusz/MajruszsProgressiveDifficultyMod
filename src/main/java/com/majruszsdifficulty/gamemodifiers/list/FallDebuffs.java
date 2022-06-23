package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class FallDebuffs extends GameModifier {
	static final CustomConfigs.ProgressiveEffect NAUSEA = new CustomConfigs.ProgressiveEffect( "Nausea", 0, 8.0 );
	static final CustomConfigs.ProgressiveEffect SLOWNESS = new CustomConfigs.ProgressiveEffect( "Slowness", 0, 6.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 1.0 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.Context<>( OnDamagedContext.Data.class, data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f ) );
		ON_DAMAGED.addConfig( NAUSEA );
		ON_DAMAGED.addConfig( SLOWNESS );
	}

	public FallDebuffs() {
		super( GameModifier.DEFAULT, "FallDebuffs", "Inflicts several debuffs when taking fall damage.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.CONFUSION, NAUSEA.getDuration(), NAUSEA.getAmplifier() );
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS.getDuration(), SLOWNESS.getAmplifier() );
		}
	}
}

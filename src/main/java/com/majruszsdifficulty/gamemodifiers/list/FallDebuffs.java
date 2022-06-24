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
	static final CustomConfigs.ProgressiveEffect NAUSEA = new CustomConfigs.ProgressiveEffect( "Nausea", ()->MobEffects.CONFUSION, 0, 8.0 );
	static final CustomConfigs.ProgressiveEffect SLOWNESS = new CustomConfigs.ProgressiveEffect( "Slowness", ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 6.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 1.0 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f ) );
		ON_DAMAGED.addConfig( NAUSEA );
		ON_DAMAGED.addConfig( SLOWNESS );
	}

	public FallDebuffs() {
		super( GameModifier.DEFAULT, "FallDebuffs", "Inflicts several debuffs when taking fall damage.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			NAUSEA.apply( damagedData.target );
			SLOWNESS.apply( damagedData.target );
		}
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class FallDebuffs extends GameModifier {
	static final Config.Effect NAUSEA = new Config.Effect( "Nausea", 0, 8.0 );
	static final Config.Effect SLOWNESS = new Config.Effect( "Slowness", 0, 6.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 1.0, false ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f ) );
		ON_DAMAGED.addConfig( NAUSEA );
		ON_DAMAGED.addConfig( SLOWNESS );
	}

	public FallDebuffs() {
		super( "FallDebuffs", "Inflicts several debuffs when taking fall damage.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.CONFUSION, NAUSEA.getDuration(), NAUSEA.getAmplifier() );
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS.getDuration(), SLOWNESS.getAmplifier() );
		}
	}
}

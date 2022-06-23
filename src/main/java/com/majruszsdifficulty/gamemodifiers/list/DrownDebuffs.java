package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

public class DrownDebuffs extends GameModifier {
	static final int MAX_TICKS = Utility.secondsToTicks( 60.0 );
	static final Config.Effect NAUSEA = new Config.Effect( "Nausea", 0, 2.0 );
	static final Config.Effect WEAKNESS = new Config.Effect( "Weakness", 0, 10.0 );
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 1.0, false ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.source.equals( DamageSource.DROWN ) ) );
		ON_DAMAGED.addConfig( NAUSEA );
		ON_DAMAGED.addConfig( WEAKNESS );
	}

	public DrownDebuffs() {
		super( "DrownDebuffs", "Inflicts several debuffs when taking drown damage (these debuffs stack).", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.CONFUSION, NAUSEA.getDuration(), NAUSEA.getAmplifier(), MAX_TICKS );
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.WEAKNESS, WEAKNESS.getDuration(), WEAKNESS.getAmplifier(), MAX_TICKS );
		}
	}
}

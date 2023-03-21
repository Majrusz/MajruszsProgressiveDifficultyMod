package com.majruszsdifficulty.gamemodifiers.configs;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.math.Range;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ProgressiveEffectConfig extends ConfigGroup {
	static final Range< Integer > AMPLIFIER = new Range<>( 1, 10 );
	static final Range< Double > DURATION = new Range<>( 1.0, 999.0 );
	static final Range< Double > MAX_DURATION = new Range<>( 5.0, 9999.0 );
	final Supplier< MobEffect > effect;
	final GameStageIntegerConfig amplifier;
	final GameStageDoubleConfig duration;
	DoubleConfig maxDuration = null;

	public ProgressiveEffectConfig( Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration ) {
		this.effect = effect;
		this.amplifier = new GameStageIntegerConfig( amplifier.normal() + 1, amplifier.expert() + 1, amplifier.master() + 1, AMPLIFIER );
		this.duration = new GameStageDoubleConfig( duration.normal(), duration.expert(), duration.master(), DURATION );

		this.addConfig( this.amplifier.name( "Amplifier" ).comment( "Level of the effect to apply." ) );
		this.addConfig( this.duration.name( "Duration" ).comment( "Duration in seconds." ) );
	}

	public ProgressiveEffectConfig( RegistryObject< ? extends MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration ) {
		this( effect::get, amplifier, duration );
	}

	public ProgressiveEffectConfig( MobEffect effect, GameStage.Integer amplifier, GameStage.Double duration ) {
		this( ()->effect, amplifier, duration );
	}

	public ProgressiveEffectConfig( RegistryObject< ? extends MobEffect > effect, int amplifier, double duration ) {
		this( effect, new GameStage.Integer( amplifier ), new GameStage.Double( duration ) );
	}

	public ProgressiveEffectConfig( MobEffect effect, int amplifier, double duration ) {
		this( effect, new GameStage.Integer( amplifier ), new GameStage.Double( duration ) );
	}

	public ProgressiveEffectConfig stackable( double maxDuration ) {
		this.maxDuration = new DoubleConfig( maxDuration, MAX_DURATION );

		this.addConfig( this.maxDuration.name( "maximum_duration" ).comment( "Maximum duration in seconds it can reach." ) );

		return this;
	}

	public void apply( LivingEntity entity ) {
		if( this.isStackable() ) {
			MobEffectHelper.tryToStack( entity, this.getEffect(), this.getDuration(), this.getAmplifier(), this.getMaxDuration() );
		} else {
			MobEffectHelper.tryToApply( entity, this.getEffect(), this.getDuration(), this.getAmplifier() );
		}
	}

	public MobEffect getEffect() {
		return this.effect.get();
	}

	public int getAmplifier() {
		return this.amplifier.getCurrentGameStageValue() - 1;
	}

	public int getDuration() {
		return Utility.secondsToTicks( this.duration.getCurrentGameStageValue() );
	}

	public boolean isStackable() {
		return this.maxDuration != null;
	}

	public int getMaxDuration() {
		return this.isStackable() ? this.maxDuration.asTicks() : 0;
	}
}

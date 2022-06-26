package com.majruszsdifficulty.gamemodifiers.configs;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class ProgressiveEffectConfig extends ConfigGroup {
	static final int MIN_AMPLIFIER = 1, MAX_AMPLIFIER = 10;
	static final double MIN_DURATION = 1.0, MAX_DURATION = 99.0;
	static final double MIN_LIMIT = 5.0, MAX_LIMIT = 999.0;
	final Supplier< MobEffect > effect;
	final GameStageIntegerConfig amplifier;
	final GameStageDoubleConfig duration;
	final Optional< DoubleConfig > maxDuration;

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration,
		Optional< Double > maxDuration
	) {
		super( groupName, "" );
		this.effect = effect;
		this.amplifier = new GameStageIntegerConfig( "Amplifier", "Level of the effect to apply.", amplifier.normal(), amplifier.expert(), amplifier.master(), MIN_AMPLIFIER, MAX_AMPLIFIER );
		this.duration = new GameStageDoubleConfig( "Duration", "Duration in seconds.", duration.normal(), duration.expert(), duration.master(), MIN_DURATION, MAX_DURATION );
		this.maxDuration = maxDuration.map( value->new DoubleConfig( "maximum_duration", "Maximum duration in seconds it can reach.", false, value, MIN_LIMIT, MAX_LIMIT ) );
		this.addConfigs( this.amplifier, this.duration );
		this.maxDuration.ifPresent( this::addConfig );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration ) {
		this( groupName, effect, amplifier, duration, Optional.empty() );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration,
		double maxDuration
	) {
		this( groupName, effect, amplifier, duration, Optional.of( maxDuration ) );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, int amplifier, GameStage.Double duration, double maxDuration ) {
		this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), duration, maxDuration );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, int amplifier, GameStage.Double duration ) {
		this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), duration );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, double duration, double maxDuration ) {
		this( groupName, effect, amplifier, new GameStage.Double( duration, duration, duration ), maxDuration );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, double duration ) {
		this( groupName, effect, amplifier, new GameStage.Double( duration, duration, duration ) );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, int amplifier, double duration, double maxDuration ) {
		this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), new GameStage.Double( duration, duration, duration ), maxDuration );
	}

	public ProgressiveEffectConfig( String groupName, Supplier< MobEffect > effect, int amplifier, double duration ) {
		this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), new GameStage.Double( duration, duration, duration ) );
	}

	public void apply( LivingEntity entity ) {
		if( this.maxDuration.isPresent() ) {
			EffectHelper.stackEffectIfPossible( entity, getEffect(), getDuration(), getAmplifier(), getMaxDuration() );
		} else {
			EffectHelper.applyEffectIfPossible( entity, getEffect(), getDuration(), getAmplifier() );
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

	public int getMaxDuration() {
		assert this.maxDuration.isPresent();

		return this.maxDuration.get().asTicks();
	}
}

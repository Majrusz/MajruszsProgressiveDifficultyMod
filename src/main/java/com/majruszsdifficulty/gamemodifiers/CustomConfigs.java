package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class CustomConfigs {
	public static class ProgressiveEffect extends Config {
		static final int MIN_AMPLIFIER = 0, MAX_AMPLIFIER = 10;
		static final double MIN_DURATION = 1.0, MAX_DURATION = 99.0;
		static final double MIN_LIMIT = 5.0, MAX_LIMIT = 999.0;
		final Supplier< MobEffect > effect;
		final GameStageIntegerConfig amplifier;
		final GameStageDoubleConfig duration;
		final Optional< DoubleConfig > maxDuration;

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration,
			Optional< Double > maxDuration
		) {
			super( groupName, "" );
			this.effect = effect;
			this.amplifier = new GameStageIntegerConfig( "Amplifier", "Level of the effect to apply.", amplifier.normal(), amplifier.expert(), amplifier.master(), MIN_AMPLIFIER, MAX_AMPLIFIER );
			this.duration = new GameStageDoubleConfig( "Duration", "Duration in seconds.", duration.normal(), duration.expert(), duration.master(), MIN_DURATION, MAX_DURATION );
			this.maxDuration = maxDuration.map( value->new DoubleConfig( "maximum_duration", "Maximum duration in seconds it can reach.", false, value, MIN_LIMIT, MAX_LIMIT ) );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration ) {
			this( groupName, effect, amplifier, duration, Optional.empty() );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, GameStage.Double duration,
			double maxDuration
		) {
			this( groupName, effect, amplifier, duration, Optional.of( maxDuration ) );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, int amplifier, GameStage.Double duration, double maxDuration ) {
			this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), duration, maxDuration );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, int amplifier, GameStage.Double duration ) {
			this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), duration );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, double duration, double maxDuration ) {
			this( groupName, effect, amplifier, new GameStage.Double( duration, duration, duration ), maxDuration );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, GameStage.Integer amplifier, double duration ) {
			this( groupName, effect, amplifier, new GameStage.Double( duration, duration, duration ) );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, int amplifier, double duration, double maxDuration ) {
			this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), new GameStage.Double( duration, duration, duration ), maxDuration );
		}

		public ProgressiveEffect( String groupName, Supplier< MobEffect > effect, int amplifier, double duration ) {
			this( groupName, effect, new GameStage.Integer( amplifier, amplifier, amplifier ), new GameStage.Double( duration, duration, duration ) );
		}

		public void apply( LivingEntity entity ) {
			if( this.maxDuration.isPresent() ) {
				EffectHelper.stackEffectIfPossible( entity, getEffect(), getAmplifier(), getDuration(), getMaxDuration() );
			} else {
				EffectHelper.applyEffectIfPossible( entity, getEffect(), getAmplifier(), getDuration() );
			}
		}

		public MobEffect getEffect() {
			return this.effect.get();
		}

		public int getAmplifier() {
			return this.amplifier.getCurrentGameStageValue();
		}

		public int getDuration() {
			return Utility.secondsToTicks( this.duration.getCurrentGameStageValue() );
		}

		public int getMaxDuration() {
			assert this.maxDuration.isPresent();

			return this.maxDuration.get().asTicks();
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfigs( this.amplifier, this.duration );
		}
	}

	public static class Bleeding extends ProgressiveEffect {
		public Bleeding( GameStage.Double duration ) {
			super( "Bleeding", Registries.BLEEDING::get, new GameStage.Integer( 0, 1, 2 ), duration );
		}

		public Bleeding( double duration ) {
			this( new GameStage.Double( duration, duration, duration ) );
		}

		public Bleeding() {
			this( 24.0 );
		}

		public void apply( OnDamagedContext.Data data ) {
			LivingEntity target = data.target;
			@Nullable LivingEntity attacker = data.attacker;
			BleedingEffect.MobEffectInstance effectInstance = new BleedingEffect.MobEffectInstance( getDuration(), getAmplifier(), false, true, attacker );
			EffectHelper.applyEffectIfPossible( target, effectInstance );

			if( target instanceof ServerPlayer targetPlayer ) {
				Registries.BASIC_TRIGGER.trigger( targetPlayer, "bleeding_received" );
				if( data.source.equals( DamageSource.CACTUS ) ) {
					Registries.BASIC_TRIGGER.trigger( targetPlayer, "cactus_bleeding" );
				}
			}
			if( attacker instanceof ServerPlayer attackerPlayer ) {
				Registries.BASIC_TRIGGER.trigger( attackerPlayer, "bleeding_inflicted" );
			}
		}
	}
}

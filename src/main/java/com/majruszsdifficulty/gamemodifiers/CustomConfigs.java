package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.goals.FollowGroupLeaderGoal;
import com.majruszsdifficulty.goals.TargetAsLeaderGoal;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.config.StringListConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public class CustomConfigs {
	public static class ProgressiveEffect extends Config {
		static final int MIN_AMPLIFIER = 0, MAX_AMPLIFIER = 9;
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
				EffectHelper.stackEffectIfPossible( entity, getEffect(), getDuration(), getAmplifier(), getMaxDuration() );
			} else {
				EffectHelper.applyEffectIfPossible( entity, getEffect(), getDuration(), getAmplifier() );
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
			this.maxDuration.ifPresent( group::addConfig );
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

	public static class MobGroups extends Config {
		static final String SIDEKICK_TAG = "MajruszsDifficultySidekick";
		static final int MIN_COUNT = 1, MAX_COUNT = 9;
		final List< Config.ItemStack > leaderConfigs = new ArrayList<>();
		final List< Config.ItemStack > sidekickConfigs = new ArrayList<>();
		final Supplier< EntityType< ? extends PathfinderMob > > mob;
		final IntegerConfig min;
		final IntegerConfig max;

		public MobGroups( String groupName, Supplier< EntityType< ? extends PathfinderMob > > mob, int min, int max ) {
			super( groupName, "" );
			this.mob = mob;
			this.min = new IntegerConfig( "min_count", "Minimum amount of mobs to spawn (leader is not considered).", false, min, MIN_COUNT, MAX_COUNT );
			this.max = new IntegerConfig( "max_count", "Maximum amount of mobs to spawn (leader is not considered).", false, max, MIN_COUNT, MAX_COUNT );
		}

		public void addLeaderConfigs( Config.ItemStack... configs ) {
			this.leaderConfigs.addAll( List.of( configs ) );
		}

		public void addSidekickConfigs( Config.ItemStack... configs ) {
			this.sidekickConfigs.addAll( List.of( configs ) );
		}

		public List< PathfinderMob > spawn( PathfinderMob leader ) {
			int sidekickAmount = Random.nextInt( getMinCount(), getMaxCount() + 1 );
			Vec3 spawnPosition = leader.position();

			List< PathfinderMob > sidekicks = new ArrayList<>();
			for( int sidekickIdx = 0; sidekickIdx < sidekickAmount; sidekickIdx++ ) {
				PathfinderMob sidekick = this.getMob().create( leader.level );
				if( sidekick == null )
					continue;

				sidekick.setPos( spawnPosition.x + Random.nextInt( -3, 4 ), spawnPosition.y + 0.5, spawnPosition.z + Random.nextInt( -3, 4 ) );
				sidekick.goalSelector.addGoal( 9, new FollowGroupLeaderGoal( sidekick, leader, 1.0, 6.0f, 5.0f ) );
				sidekick.targetSelector.addGoal( 9, new TargetAsLeaderGoal( sidekick, leader ) );
				sidekick.getPersistentData().putBoolean( SIDEKICK_TAG, true );

				leader.level.addFreshEntity( sidekick );
				sidekicks.add( sidekick );
			}
			applyConfigs( leader, sidekicks );

			return sidekicks;
		}

		public EntityType< ? extends PathfinderMob > getMob() {
			return this.mob.get();
		}

		public int getMinCount() {
			return Math.min( this.min.get(), this.max.get() );
		}

		public int getMaxCount() {
			return Math.max( this.min.get(), this.max.get() );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfigs( this.min, this.max );
		}

		private void applyConfigs( PathfinderMob leader, List< PathfinderMob > sidekicks ) {
			double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( leader );
			for( Config.ItemStack config : this.leaderConfigs ) {
				config.tryToEquip( leader, clampedRegionalDifficulty );
			}
			for( Config.ItemStack config : this.sidekickConfigs ) {
				for( PathfinderMob sidekick : sidekicks ) {
					config.tryToEquip( sidekick, clampedRegionalDifficulty );
				}
			}
		}
	}

	public static class StageProgress extends Config {
		final StringListConfig triggeringEntities;
		final StringListConfig triggeringDimensions;

		public StageProgress( String groupName, String groupComment, String defaultEntity, String defaultDimension ) {
			super( groupName, groupComment );
			this.triggeringEntities = new StringListConfig( "triggering_entities", "List of entities which start the game stage after killing them.", false, defaultEntity );
			this.triggeringDimensions = new StringListConfig( "triggering_dimensions", "List of dimensions which start the game stage after entering them.", false, defaultDimension );
		}

		public boolean entityTriggersChange( ResourceLocation entityLocation ) {
			return this.triggeringEntities.contains( entityLocation.toString() );
		}

		public boolean dimensionTriggersChange( ResourceLocation dimensionLocation ) {
			return this.triggeringDimensions.contains( dimensionLocation.toString() );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfigs( this.triggeringEntities, this.triggeringDimensions );
		}
	}
}

package com.majruszsdifficulty.entity;

import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.time.TimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class Giant extends Monster {
	public static EntityType< Giant > createEntityType() {
		return EntityType.Builder.of( Giant::new, MobCategory.MONSTER )
			.sized( 3.0f, 10.0f )
			.build( "giant" );
	}

	public static AttributeSupplier createAttributes() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 200.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 10.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 1.0 )
			.build();
	}

	public Giant( EntityType< ? extends Monster > entityType, Level level ) {
		super( entityType, level );

		this.setPathfindingMalus( BlockPathTypes.LEAVES, 0.0f );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 40, 60 );
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public float maxUpStep() {
		return 4.0f;
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() ) {
			SoundEmitter.of( sound )
				.volume( SoundEmitter.randomized( volume * 1.25f ) )
				.pitch( SoundEmitter.randomized( pitch * 0.6f ) )
				.source( this.getSoundSource() )
				.position( this.position() )
				.emit( this.level() );
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_AMBIENT;
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
		return 8.5f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource source ) {
		return SoundEvents.ZOMBIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIE_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.ZOMBIE_STEP, 0.15f, 1.0f );
	}

	@Override
	public void tick() {
		super.tick();

		if( this.isSunBurnTick() ) {
			this.setSecondsOnFire( 8 );
		}
		if( TimeHelper.haveSecondsPassed( 0.5 ) ) {
			EntityHelper.destroyBlocks( this, this.getBoundingBox().inflate( 0.6 ), ( blockPos, blockState )->{
				return blockState.getBlock() instanceof LeavesBlock;
			} );
			EntityHelper.destroyBlocks( this, this.getBoundingBox(), ( blockPos, blockState )->{
				return blockState.getCollisionShape( this.level(), blockPos ).isEmpty();
			} );
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new GiantMeleeAttackGoal( this ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
	}

	private static class GiantMeleeAttackGoal extends MeleeAttackGoal {
		private final Giant giant;
		private int raiseArmTicks;

		public GiantMeleeAttackGoal( Giant giant ) {
			super( giant, 1.0, true );

			this.giant = giant;
		}

		@Override
		public void start() {
			super.start();

			this.raiseArmTicks = 0;
		}

		@Override
		public void stop() {
			super.stop();

			this.giant.setAggressive( false );
		}

		@Override
		public void tick() {
			super.tick();

			++this.raiseArmTicks;
			this.giant.setAggressive( this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2 );
		}

		@Override
		protected double getAttackReachSqr( LivingEntity target ) {
			return 2.0 * this.mob.getBbWidth() * this.mob.getBbWidth() + target.getBbWidth();
		}

		@Override
		protected void checkAndPerformAttack( LivingEntity target, double distance ) {
			if( distance <= this.getAttackReachSqr( target ) && this.getTicksUntilNextAttack() <= 0 ) {
				double knockbackMultiplier = 1.0 - target.getAttributeValue( Attributes.KNOCKBACK_RESISTANCE );
				this.resetAttackCooldown();
				this.mob.swing( InteractionHand.MAIN_HAND );
				this.mob.doHurtTarget( target );
				target.addDeltaMovement( EntityHelper.getDirection2d( this.giant ).mul( -1.0 ).add( 0.0, 1.0, 0.0 ).mul( knockbackMultiplier ).vec3() );
			}
		}
	}
}

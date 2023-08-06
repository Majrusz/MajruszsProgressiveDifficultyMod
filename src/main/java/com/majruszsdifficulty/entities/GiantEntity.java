package com.majruszsdifficulty.entities;

import com.mlib.Random;
import com.mlib.effects.SoundHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.time.TimeHelper;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.function.Supplier;

public class GiantEntity extends Monster {
	public static Supplier< EntityType< GiantEntity > > createSupplier() {
		return ()->EntityType.Builder.of( GiantEntity::new, MobCategory.MONSTER ).sized( 3.0f, 10.0f ).build( "giant" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 200.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 10.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 1.0 )
			.add( ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0 )
			.build();
	}

	public GiantEntity( EntityType< ? extends GiantEntity > type, Level world ) {
		super( type, world );

		this.setMaxUpStep( 3.0f );
		this.setPathfindingMalus( BlockPathTypes.LEAVES, 0.0f );
	}

	@Override
	public void tick() {
		super.tick();

		if( this.isSunBurnTick() ) {
			this.setSecondsOnFire( 8 );
		}
		if( TimeHelper.hasServerSecondsPassed( 0.5 ) ) {
			EntityHelper.destroyBlocks( this, this.getBoundingBox().inflate( 0.6 ), ( blockPos, blockState )->{
				return blockState.getBlock() instanceof LeavesBlock;
			} );
			EntityHelper.destroyBlocks( this, this.getBoundingBox(), ( blockPos, blockState )->{
				return blockState.getCollisionShape( this.level(), blockPos ).isEmpty();
			} );
		}
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
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() ) {
			float randomizedVolume = SoundHandler.randomized( volume * 1.25f ).get();
			float randomizedPitch = SoundHandler.randomized( pitch, 0.7f, 0.85f ).get();

			this.level().playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), randomizedVolume, randomizedPitch );
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 2, new GiantAttackGoal( this, 1.0, true ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_AMBIENT;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 8.5f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
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

	public static class GiantAttackGoal extends MeleeAttackGoal {
		private final GiantEntity giant;
		private int raiseArmTicks;

		public GiantAttackGoal( GiantEntity giant, double speedModifier, boolean followingTargetEvenIfNotSeen ) {
			super( giant, speedModifier, followingTargetEvenIfNotSeen );

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
				this.resetAttackCooldown();
				this.mob.swing( InteractionHand.MAIN_HAND );
				this.mob.doHurtTarget( target );
				target.addDeltaMovement( new Vec3( 0.0, 0.5, 0.0 ) );
			}
		}
	}
}

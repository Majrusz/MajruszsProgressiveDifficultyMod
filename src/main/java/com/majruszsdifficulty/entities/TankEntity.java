package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.PacketHandler;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.effects.SoundHandler;
import com.mlib.entities.CustomSkills;
import com.mlib.entities.EntityHelper;
import com.mlib.entities.ICustomSkillProvider;
import com.mlib.goals.CustomMeleeGoal;
import com.mlib.math.AnyPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/** A new tough undead similar to the Iron Golem. */
public class TankEntity extends Monster implements ICustomSkillProvider< TankEntity.Skills > {
	public final Skills skills = new Skills( this );

	public static Supplier< EntityType< TankEntity > > createSupplier() {
		return ()->EntityType.Builder.of( TankEntity::new, MobCategory.MONSTER ).sized( 0.99f, 2.7f ).build( "tank" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 140.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 6.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 3.5 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.75 )
			.build();
	}

	public TankEntity( EntityType< ? extends TankEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public Skills getCustomSkills() {
		return this.skills;
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 10, 17 );
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( this.isSilent() ) {
			return;
		}
		float randomizedVolume = SoundHandler.randomized( volume * 1.25f ).get();
		float randomizedPitch = SoundHandler.randomized( pitch * 0.75f ).get();

		this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), randomizedVolume, randomizedPitch );
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new CustomMeleeGoal<>( this, 1.0, true ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Warden.class, true ) );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SKELETON_AMBIENT;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 2.35f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
		return SoundEvents.SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SKELETON_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.SKELETON_STEP, 0.15f, 1.0f );
	}

	@Override
	public void tick() {
		super.tick();

		this.skills.tick();
		if( this.isSunBurnTick() ) {
			this.setSecondsOnFire( 8 );
		}
	}

	public static class Skills extends CustomSkills< SkillType > {
		public Skills( PathfinderMob mob ) {
			super( mob, PacketHandler.CHANNEL, SkillMessage::new );
		}

		@Override
		public boolean tryToStart( LivingEntity entity, double distanceSquared ) {
			if( Math.sqrt( distanceSquared ) >= 2.5 ) {
				return false;
			}

			if( Random.tryChance( 0.25 ) ) {
				this.start( SkillType.HEAVY_ATTACK, Utility.secondsToTicks( 0.9 ) )
					.onRatio( 0.55f, ()->{
						if( !( this.mob.level instanceof ServerLevel level ) )
							return;

						Vec3 position = this.getSpecialAttackPosition( this.mob.position(), entity.position() );
						this.hurtAllEntitiesInRange( level, position );
						this.spawnGroundParticles( level, position );
						this.playHitSound();
					} );
			} else {
				this.start( Random.tryChance( 0.5 ) ? SkillType.STANDARD_LEFT_ATTACK : SkillType.STANDARD_RIGHT_ATTACK, Utility.secondsToTicks( 0.6 ) )
					.onRatio( 0.45f, ()->{
						if( Math.sqrt( this.mob.distanceTo( entity ) ) < 2.5 ) {
							this.tryToHitEntity( entity );
							this.playHitSound();
						}
					} );
			}

			return true;
		}

		private void hurtAllEntitiesInRange( ServerLevel level, Vec3 position ) {
			List< LivingEntity > entities = EntityHelper.getEntitiesInSphere( LivingEntity.class, level, position, 3.0, entity->!entity.is( this.mob ) );
			for( LivingEntity entity : entities ) {
				if( !this.mob.canAttack( entity, TargetingConditions.DEFAULT ) )
					continue;

				this.mob.doHurtTarget( entity );
				if( entity instanceof ServerPlayer player && player.isBlocking() ) {
					player.disableShield( true );
				}
			}
		}

		private void spawnGroundParticles( ServerLevel level, Vec3 position ) {
			Optional< BlockState > blockState = this.getBlockStateBelowPosition( level, position );
			BlockParticleOption blockParticleOption = new BlockParticleOption( ParticleTypes.BLOCK, blockState.orElse( Blocks.DIRT.defaultBlockState() ) ).setPos( new BlockPos( position ) );

			level.sendParticles( blockParticleOption, position.x, position.y + 0.25, position.z, 120, 1.0, 0.25, 1.0, 0.5 );
		}

		protected Vec3 getSpecialAttackPosition( Vec3 tankPosition, Vec3 targetPosition ) {
			return AnyPos.from( targetPosition ).sub( tankPosition ).norm().mul( 1.75 ).add( tankPosition ).vec3();
		}

		private Optional< BlockState > getBlockStateBelowPosition( Level level, Vec3 position ) {
			int y = ( int )position.y;
			while( y > level.getMinBuildHeight() ) {
				BlockState blockState = level.getBlockState( new BlockPos( position.x, y, position.z ) );
				if( !blockState.isAir() )
					return Optional.of( blockState );

				--y;
			}

			return Optional.empty();
		}

		private void tryToHitEntity( LivingEntity entity ) {
			if( !this.mob.canAttack( entity, TargetingConditions.DEFAULT ) ) {
				return;
			}

			this.mob.doHurtTarget( entity );
		}

		private void playHitSound() {
			this.mob.playSound( SoundEvents.SKELETON_HURT, 0.75f, 0.9f );
		}
	}

	public static class SkillMessage extends CustomSkills.Message< SkillType > {
		public SkillMessage( Entity entity, int ticks, SkillType skillType ) {
			super( entity, ticks, skillType, SkillType::values );
		}

		public SkillMessage() {
			super( SkillType::values );
		}
	}

	public enum SkillType {
		STANDARD_LEFT_ATTACK,
		STANDARD_RIGHT_ATTACK,
		HEAVY_ATTACK
	}
}

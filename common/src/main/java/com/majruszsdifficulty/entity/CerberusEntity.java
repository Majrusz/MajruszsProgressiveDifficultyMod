package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.Animations;
import com.majruszlibrary.animations.AnimationsDef;
import com.majruszlibrary.animations.IAnimableEntity;
import com.majruszlibrary.contexts.OnEntityDamaged;
import com.majruszlibrary.contexts.OnEntityEffectCheck;
import com.majruszlibrary.contexts.OnEntityTicked;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.modhelper.LazyResource;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CerberusEntity extends Monster implements IAnimableEntity {
	private static final LazyResource< AnimationsDef > ANIMATIONS = MajruszsDifficulty.HELPER.load( "cerberus_animation", AnimationsDef.class, PackType.SERVER_DATA );
	private final Animations animations = Animations.create();

	static {
		OnEntityDamaged.listen( CerberusEntity::applyWither )
			.addCondition( OnEntityDamaged::isDirect )
			.addCondition( data->data.attacker instanceof CerberusEntity );

		OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
			.addCondition( data->data.effect.equals( MobEffects.WITHER ) )
			.addCondition( data->data.entity instanceof CerberusEntity );

		OnEntityTicked.listen( CerberusEntity::spawnParticle )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.2f ) )
			.addCondition( data->data.entity instanceof CerberusEntity );
	}

	public static EntityType< CerberusEntity > createEntityType() {
		return EntityType.Builder.of( CerberusEntity::new, MobCategory.MONSTER )
			.sized( 1.2f, 1.75f )
			.build( "cerberus" );
	}

	public static AttributeSupplier createAttributes() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 240.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.build();
	}

	public CerberusEntity( EntityType< ? extends Monster > entityType, Level level ) {
		super( entityType, level );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 26, 46 );
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public float maxUpStep() {
		return 1.6f;
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() ) {
			SoundEmitter.of( sound )
				.volume( SoundEmitter.randomized( volume ) )
				.pitch( SoundEmitter.randomized( pitch * 0.75f ) )
				.source( this.getSoundSource() )
				.position( this.position() )
				.emit( this.level() );
		}
	}

	@Override
	public void tick() {
		super.tick();

		if( this.isSunBurnTick() ) {
			this.setSecondsOnFire( 8 );
		}
	}

	@Override
	public AnimationsDef getAnimationsDef() {
		return ANIMATIONS.get();
	}

	@Override
	public Animations getAnimations() {
		return this.animations;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.WOLF_GROWL;
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
		return 1.6f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource source ) {
		return SoundEvents.WITHER_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WITHER_SKELETON_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.WITHER_SKELETON_STEP, 0.15f, 1.0f );
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new CerberusMeleeAttackGoal( this ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		// this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Mob.class, 2, true, false, this::isValidTarget ) ); TODO
	}

	private static void applyWither( OnEntityDamaged data ) {
		data.target.addEffect( new MobEffectInstance( MobEffects.WITHER, TimeHelper.toTicks( 10.0 ), 1 ) );
	}

	private static void spawnParticle( OnEntityTicked data ) {
		ParticleEmitter.of( ParticleTypes.SMOKE )
			.sizeBased( data.entity )
			.count( 1 )
			.offset( ()->new Vec3( 0.25, 0.5, 0.25 ) )
			.speed( ParticleEmitter.speed( 0.001f, 0.002f ) )
			.emit( data.getLevel() );
	}

	private static class CerberusMeleeAttackGoal extends MeleeAttackGoal {
		private final CerberusEntity cerberus;
		private int fireballCooldownLeft = 0;

		public CerberusMeleeAttackGoal( CerberusEntity cerberus ) {
			super( cerberus, 1.5, true );

			this.cerberus = cerberus;
		}

		@Override
		public void tick() {
			super.tick();

			this.cerberus.setAggressive( !this.cerberus.animations.isEmpty() );
			this.fireballCooldownLeft = Math.max( this.fireballCooldownLeft - 1, 0 );
		}

		@Override
		protected void checkAndPerformAttack( LivingEntity target, double distanceSqr ) {
			if( !this.cerberus.animations.isEmpty() || !( target.level() instanceof ServerLevel ) ) {
				return;
			}

			if( distanceSqr > 100.0f && this.fireballCooldownLeft == 0 ) {
				this.fireballCooldownLeft = TimeHelper.toTicks( 10.0 );
				this.resetAttackCooldown();
				this.useFireBreath( target );
			} else if( distanceSqr < this.getAttackReachSqr( target ) ) {
				this.resetAttackCooldown();
				this.bite( target );
			}
		}

		private void useFireBreath( LivingEntity target ) {
			this.cerberus.playAnimation( "fire_breath" )
				.addCallback( 3, ()->this.spawnFireballTowards( target ) )
				.addCallback( 11, ()->this.spawnFireballTowards( target ) )
				.addCallback( 19, ()->this.spawnFireballTowards( target ) );
		}

		private void bite( LivingEntity target ) {
			this.cerberus.playAnimation( "bite" )
				.addCallback( 2, ()->this.cerberus.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 0.8f ) )
				.addCallback( 3, ()->this.cerberus.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 0.7f ) )
				.addCallback( 4, ()->this.cerberus.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 0.9f ) )
				.addCallback( 7, ()->this.hitAllNearbyEntities( ( ServerLevel )target.level() ) );
		}

		private void hitAllNearbyEntities( ServerLevel level ) {
			Vec3 position = AnyPos.from( this.cerberus.position() ).add( EntityHelper.getDirection2d( this.cerberus ).mul( 1.5 ) ).vec3();
			for( LivingEntity entity : EntityHelper.getEntitiesNearby( LivingEntity.class, level, position, 2.0 ) ) {
				if( entity.equals( this.cerberus ) ) {
					continue;
				}

				if( this.cerberus.canAttack( entity, TargetingConditions.DEFAULT ) ) {
					this.cerberus.doHurtTarget( entity );
					if( entity instanceof ServerPlayer player && player.isBlocking() ) {
						player.disableShield( true );
					}
				}
			}
		}

		private void spawnFireballTowards( LivingEntity target ) {
			Vec3 offset = AnyPos.from( target.position() ).sub( this.mob.position() ).vec3();
			for( double angle : new double[]{ -30.0, 0.0, 30.0 } ) {
				Vec3 power = AnyPos.from( offset ).mul( Random.nextVector( 0.8, 1.2, 0.8, 1.2, 0.8, 1.2 ) ).vec3();
				double cos = Math.cos( Math.toRadians( angle ) ), sin = Math.sin( Math.toRadians( angle ) );
				Vec3 normalized = AnyPos.from( offset ).norm().vec3();
				normalized = new Vec3( cos * normalized.x - sin * normalized.z, normalized.y, sin * normalized.x + cos * normalized.z );
				Vec3 spawnPosition = AnyPos.from( this.mob.position() ).add( normalized ).add( 0.0, Random.nextDouble( 1.2, 1.5 ), 0.0 ).vec3();
				SmallFireball fireball = new SmallFireball( this.mob.level(), this.mob, power.x, power.y, power.z );
				fireball.setPos( spawnPosition.x, spawnPosition.y, spawnPosition.z );
				fireball.setDeltaMovement( AnyPos.from( power ).norm().mul( 0.25 ).vec3() );

				this.mob.level().addFreshEntity( fireball );
			}

			SoundEmitter.of( SoundEvents.FURNACE_FIRE_CRACKLE )
				.position( this.mob.position() )
				.emit( this.mob.level() );
		}
	}
}

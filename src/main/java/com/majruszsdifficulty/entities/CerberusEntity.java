package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.EffectConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.entities.CustomSkills;
import com.mlib.entities.EntityHelper;
import com.mlib.entities.ICustomSkillProvider;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnEffectApplicable;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.goals.CustomMeleeGoal;
import com.mlib.math.AnyPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.function.Supplier;

public class CerberusEntity extends Monster implements ICustomSkillProvider< CerberusEntity.Skills > {
	static final String GROUP_ID = "Cerberus";

	static {
		ModConfigs.init( Registries.Groups.MOBS, GROUP_ID ).name( "Cerberus" );
	}

	public final Skills skills = new Skills( this );

	public static Supplier< EntityType< CerberusEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CerberusEntity::new, MobCategory.MONSTER ).sized( 1.2f, 1.75f ).build( "cerberus" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 240.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.28 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.add( ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.5 )
			.build();
	}

	public CerberusEntity( EntityType< ? extends CerberusEntity > type, Level world ) {
		super( type, world );

		this.setMaxUpStep( 1.0f );
	}

	@Override
	public Skills getCustomSkills() {
		return this.skills;
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 60, 90 );
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

		float randomizedVolume = SoundHandler.randomized( volume ).get();
		float randomizedPitch = SoundHandler.randomized( pitch * 0.75f ).get();
		this.level().playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), randomizedVolume, randomizedPitch );
	}

	@Override
	public void tick() {
		super.tick();

		this.skills.tick();
		if( this.isSunBurnTick() ) {
			this.setSecondsOnFire( 8 );
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new CustomMeleeGoal<>( this, 1.5, true ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f, 1.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Mob.class, 2, true, false, this::isValidTarget ) );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.WOLF_GROWL;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 1.6f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
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

	private boolean isValidTarget( LivingEntity entity ) {
		return !entity.getMobType().equals( MobType.UNDEAD )
			&& !Registries.getUndeadArmyManager().isPartOfUndeadArmy( entity );
	}

	public static class Skills extends CustomSkills< SkillType > {
		int fireballCooldownLeft = 0;

		public Skills( PathfinderMob mob ) {
			super( mob, PacketHandler.CHANNEL, SkillMessage::new );
		}

		@Override
		public boolean tryToStart( LivingEntity entity, double distanceSquared ) {
			if( !( this.mob.level() instanceof ServerLevel level ) )
				return false;

			double distance = Math.sqrt( distanceSquared );
			if( distance < 3.5 && this.mob.canAttack( entity, TargetingConditions.DEFAULT ) ) {
				Vec3 position = this.getAttackPosition( this.mob.position(), entity.position() );
				this.start( SkillType.BITE, Utility.secondsToTicks( 0.7 ) )
					.onTick( 2, ()->this.mob.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 0.85f ) )
					.onTick( 3, ()->this.mob.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 0.7f ) )
					.onTick( 4, ()->this.mob.playSound( SoundEvents.WOLF_AMBIENT, 0.5f, 1.0f ) )
					.onRatio( 0.55f, ()->this.hurtAllEntitiesInRange( level, position ) );

				return true;
			} else if( distance >= 10.0 && this.fireballCooldownLeft == 0 ) {
				this.start( SkillType.FIRE_BREATH, Utility.secondsToTicks( 1.4 ) )
					.onRatio( 0.25f, ()->this.spawnFireballTowards( entity ) )
					.onRatio( 0.5f, ()->this.spawnFireballTowards( entity ) )
					.onRatio( 0.75f, ()->this.spawnFireballTowards( entity ) );
				this.fireballCooldownLeft = Utility.secondsToTicks( 10.0 );

				return true;
			}

			return false;
		}

		@Override
		public void tick() {
			super.tick();

			this.fireballCooldownLeft = Math.max( this.fireballCooldownLeft - 1, 0 );
		}

		private void hurtAllEntitiesInRange( ServerLevel level, Vec3 position ) {
			List< LivingEntity > entities = EntityHelper.getEntitiesInSphere( LivingEntity.class, level, position, 2.5, entity->!entity.is( this.mob ) );
			for( LivingEntity entity : entities ) {
				if( !this.mob.canAttack( entity, TargetingConditions.DEFAULT ) )
					continue;

				this.mob.doHurtTarget( entity );
				if( entity instanceof ServerPlayer player && player.isBlocking() ) {
					player.disableShield( true );
				}
			}
		}

		private Vec3 getAttackPosition( Vec3 pos1, Vec3 pos2 ) {
			return AnyPos.from( pos2 ).sub( pos1 ).norm().mul( 1.75 ).add( pos1 ).vec3();
		}

		private void spawnFireballTowards( LivingEntity target ) {
			Vec3 offset = AnyPos.from( target.position() ).sub( this.mob.position() ).vec3();
			for( double angle : new double[]{ -30.0, 0.0, 30.0 } ) {
				Vec3 power = AnyPos.from( offset ).mul( Random.getRandomVector( 0.8, 1.2, 0.8, 1.2, 0.8, 1.2 ) ).vec3();
				double cos = Math.cos( Math.toRadians( angle ) ), sin = Math.sin( Math.toRadians( angle ) );
				Vec3 normalized = AnyPos.from( offset ).norm().vec3();
				normalized = new Vec3( cos * normalized.x - sin * normalized.z, normalized.y, sin * normalized.x + cos * normalized.z );
				Vec3 spawnPosition = AnyPos.from( this.mob.position() ).add( normalized ).add( 0.0, Random.nextDouble( 1.2, 1.5 ), 0.0 ).vec3();
				SmallFireball fireball = new SmallFireball( this.mob.level(), this.mob, power.x, power.y, power.z );
				fireball.setPos( spawnPosition.x, spawnPosition.y, spawnPosition.z );
				fireball.setDeltaMovement( AnyPos.from( power ).norm().mul( 0.25 ).vec3() );

				this.mob.level().addFreshEntity( fireball );
			}

			SoundHandler.SMELT.play( this.mob.level(), this.mob.position() );
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

	@AutoInstance
	public static class WitherAttack {
		final EffectConfig wither = new EffectConfig( MobEffects.WITHER, 1, 10.0 );

		public WitherAttack() {
			ConfigGroup group = ModConfigs.registerSubgroup( GROUP_ID );

			OnDamaged.listen( this::applyWither )
				.addCondition( OnDamaged.isDirect() )
				.addCondition( OnDamaged.dealtAnyDamage() )
				.addCondition( Condition.predicate( data->data.attacker instanceof CerberusEntity ) )
				.addConfig( this.wither.name( "Wither" ) )
				.insertTo( group );

			OnEffectApplicable.listen( this::cancelEffect )
				.addCondition( Condition.predicate( data->data.effect.equals( MobEffects.WITHER ) ) )
				.addCondition( Condition.predicate( data->data.entity instanceof CerberusEntity ) )
				.insertTo( group );

			OnEntityTick.listen( this::spawnParticle )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.< OnEntityTick.Data > cooldown( 4, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.predicate( data->data.entity instanceof CerberusEntity ) )
				.insertTo( group );
		}

		private void applyWither( OnDamaged.Data data ) {
			this.wither.apply( data.target );
		}

		private void cancelEffect( OnEffectApplicable.Data data ) {
			data.event.setResult( Event.Result.DENY );
		}

		private void spawnParticle( OnEntityTick.Data data ) {
			ParticleHandler.SMOKE.spawn( data.getServerLevel(), data.entity.position().add( 0.0, 0.75, 0.0 ), 3, ()->new Vec3( 0.25, 0.5, 0.25 ) );
		}
	}

	public enum SkillType {
		BITE,
		FIRE_BREATH
	}
}

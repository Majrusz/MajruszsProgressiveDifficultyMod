package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.effects.SoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

import java.util.function.Supplier;

public class CerberusEntity extends Monster {

	public static Supplier< EntityType< CerberusEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CerberusEntity::new, MobCategory.MONSTER ).sized( 1.4f, 1.99f ).build( "cerberus" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 140.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 14.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.add( ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0 )
			.build();
	}

	public CerberusEntity( EntityType< ? extends CerberusEntity > type, Level world ) {
		super( type, world );

		this.xpReward = 17;
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 17 );
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
		this.goalSelector.addGoal( 1, new MeleeAttackGoal( this, 1.5, false ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f, 1.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Mob.class, 2, true, false, CerberusEntity::isValidTarget ) );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SKELETON_AMBIENT;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 1.8f;
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

	private static boolean isValidTarget( LivingEntity entity ) {
		return !Registries.UNDEAD_ARMY_MANAGER.isPartOfUndeadArmy( entity )
			&& !( entity instanceof CerberusEntity );
	}
}

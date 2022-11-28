package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.Random;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SpiderQueenEntity extends Monster {
	public static Supplier< EntityType< SpiderQueenEntity > > createSupplier() {
		return ()->EntityType.Builder.of( SpiderQueenEntity::new, MobCategory.MONSTER )
			.sized( 1.9f, 1.5f )
			.build( "spider_queen" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 140.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 3.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.75 )
			.build();
	}

	public SpiderQueenEntity( EntityType< ? extends SpiderQueenEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	protected void registerGoals() {
		// this.goalSelector.addGoal( 2, new TankAttackGoal( this ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Warden.class, true ) );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 17 );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SPIDER_AMBIENT;
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 0.75f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
		return SoundEvents.SPIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SPIDER_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.SPIDER_STEP, 0.15f, 1.0f );
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume * 1.25f, pitch * 0.75f );
	}

	@Override
	public void aiStep() {
		if( this.isSunBurnTick() && !UndeadArmyManager.isUndeadArmy( this ) )
			this.setSecondsOnFire( 8 );

		super.aiStep();
	}
}

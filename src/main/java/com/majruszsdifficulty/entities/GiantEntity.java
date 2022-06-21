package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.goals.GiantAttackGoal;
import com.mlib.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/** Slightly modified the Giant mob. */
public class GiantEntity extends Zombie {
	public static final float scale = 5.0f; // by default minecraft giants have 6.0f scale but I want to make it a little bit smaller

	public static Supplier< EntityType< GiantEntity > > createSupplier() {
		return ()->EntityType.Builder.of( GiantEntity::new, MobCategory.MONSTER ).sized( 0.6f * scale, 2.0f * scale ).build( "giant" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 120.0D )
			.add( Attributes.MOVEMENT_SPEED, 0.25D )
			.add( Attributes.ATTACK_DAMAGE, 10.0D )
			.add( Attributes.FOLLOW_RANGE, 40.0D )
			.add( Attributes.ATTACK_KNOCKBACK, 2.0D )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5D )
			.add( Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D )
			.build();
	}

	public GiantEntity( EntityType< ? extends Zombie > type, Level world ) {
		super( type, world );
		this.xpReward = 15;
		this.setBaby( false );
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );
		this.applyEntityAI();
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 15 );
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 10.440001f;
	}

	@Override
	protected void handleAttributes( float difficulty ) {
		// this is empty to disable a Zombie behaviour to call reinforcements
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume * 1.25f, pitch * 0.75f );
	}

	protected void applyEntityAI() {
		this.goalSelector.addGoal( 2, new GiantAttackGoal( this, 1.0, false ) );
		this.goalSelector.addGoal( 6, new MoveThroughVillageGoal( this, 1.0, true, 4, this::canBreakDoors ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.targetSelector.addGoal( 1, ( new HurtByTargetGoal( this ) ).setAlertOthers( ZombifiedPiglin.class ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, AbstractVillager.class, false ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
		this.targetSelector.addGoal( 5, new NearestAttackableTargetGoal<>( this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR ) );
	}
}
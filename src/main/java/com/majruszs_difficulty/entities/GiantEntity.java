package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.goals.GiantAttackGoal;
import com.mlib.MajruszLibrary;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/** Entity that adds a Giant again to the game. */
public class GiantEntity extends ZombieEntity {
	public static final float scale = 5.0f; // by default minecraft giants have 6.0f scale but I want to make it a little bit smaller
	public static final EntityType< GiantEntity > type;

	static {
		type = EntityType.Builder.create( GiantEntity::new, EntityClassification.MONSTER )
			.size( 0.6f * scale, 2.0f * scale )
			.build( MajruszsDifficulty.getLocation( "giant" )
				.toString() );
	}

	public GiantEntity( EntityType< ? extends ZombieEntity > type, World world ) {
		super( type, world );
		this.experienceValue = 15;
		this.setChild( false );
	}

	/** This method is empty to disable ZombieEntity method which applies attributes like 'Zombie Reinforcement'. */
	@Override
	protected void applyAttributeBonuses( float difficulty ) {}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntitySize sizeIn ) {
		return 10.440001f;
	}

	/** Registration of the entity's basic goals. */
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal( 8, new LookAtGoal( this, PlayerEntity.class, 8.0F ) );
		this.goalSelector.addGoal( 8, new LookRandomlyGoal( this ) );
		this.applyEntityAI();
	}

	/** Registration of the entity's artificial intelligence goals. */
	protected void applyEntityAI() {
		this.goalSelector.addGoal( 2, new GiantAttackGoal( this, 1.0D, false ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomWalkingGoal( this, 1.0D ) );
		this.targetSelector.addGoal( 1, ( new HurtByTargetGoal( this ) ).setCallsForHelp( ZombifiedPiglinEntity.class ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, PlayerEntity.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, AbstractVillagerEntity.class, false ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolemEntity.class, true ) );
		this.targetSelector.addGoal( 5,
			new NearestAttackableTargetGoal<>( this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY )
		);
	}

	/** Calculating experience points after killing this entity. */
	@Override
	protected int getExperiencePoints( PlayerEntity player ) {
		this.experienceValue += MajruszLibrary.RANDOM.nextInt( 15 );

		return super.getExperiencePoints( player );
	}

	/** Modulation of basic Zombie sounds. */
	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.world.playSound( null, this.getPosX(), this.getPosY(), this.getPosZ(), sound, this.getSoundCategory(), volume * 1.25f,
				pitch * 0.75f
			);
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 120.0D )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.25D )
			.createMutableAttribute( Attributes.ATTACK_DAMAGE, 10.0D )
			.createMutableAttribute( Attributes.FOLLOW_RANGE, 40.0D )
			.createMutableAttribute( Attributes.ATTACK_KNOCKBACK, 2.0D )
			.createMutableAttribute( Attributes.KNOCKBACK_RESISTANCE, 0.5D )
			.createMutableAttribute( Attributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0D )
			.create();
	}
}

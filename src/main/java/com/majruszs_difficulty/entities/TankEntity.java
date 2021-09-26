package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.goals.TankAttackGoal;
import com.mlib.MajruszLibrary;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** New undead huge skeleton. */
public class TankEntity extends Monster {
	public static final EntityType< TankEntity > type;

	static {
		type = EntityType.Builder.of( TankEntity::new, MobCategory.MONSTER )
			.sized( 1.1f, 2.7f )
			.build( MajruszsDifficulty.getLocation( "tank" ).toString() );
	}

	public TankEntity( EntityType< ? extends TankEntity > type, Level world ) {
		super( type, world );
		this.xpReward = 17;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new TankAttackGoal( this ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	@Override
	protected int getExperienceReward( Player player ) {
		return super.getExperienceReward( player ) + MajruszLibrary.RANDOM.nextInt( 17 );
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 2.35f;
	}

	/*@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume * 1.25f, pitch * 0.75f );
	}*/

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 100.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 40.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 2.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.build();
	}
}

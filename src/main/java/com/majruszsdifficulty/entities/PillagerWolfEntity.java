package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Entity that is more powerful version of Wolf and always hostile. */
public class PillagerWolfEntity extends Wolf {
	public static final EntityType< PillagerWolfEntity > type;

	static {
		type = EntityType.Builder.of( PillagerWolfEntity::new, MobCategory.MONSTER )
			.sized( 0.625f, 0.85f )
			.build( MajruszsDifficulty.getLocation( "pillager_wolf" ).toString() );
	}

	public PillagerWolfEntity( EntityType< ? extends Wolf > type, Level world ) {
		super( type, world );
		this.xpReward = 4;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new FloatGoal( this ) );
		this.goalSelector.addGoal( 5, new MeleeAttackGoal( this, 1.0D, true ) );
		this.goalSelector.addGoal( 8, new WaterAvoidingRandomStrollGoal( this, 1.0D ) );
		this.goalSelector.addGoal( 10, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
		this.goalSelector.addGoal( 10, new RandomLookAroundGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Villager.class, true ) );
		this.targetSelector.addGoal( 4, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	/** Makes wolf unable to be tamed. */
	@Override
	public boolean isTame() {
		return true;
	}

	@Override
	public boolean removeWhenFarAway( double distanceToClosestPlayer ) {
		return distanceToClosestPlayer > 100.0;
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 12.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3125 )
			.add( Attributes.ATTACK_DAMAGE, 4.0 )
			.build();
	}
}

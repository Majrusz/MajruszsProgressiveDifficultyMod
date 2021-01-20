package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/** Entity that is more powerful version of Wolf and always hostile. */
public class PillagerWolfEntity extends WolfEntity {
	public static final EntityType< PillagerWolfEntity > type;

	static {
		type = EntityType.Builder.create( PillagerWolfEntity::new, EntityClassification.MONSTER )
			.size( 0.625f, 0.85f )
			.build( MajruszsHelper.getResource( "pillager_wolf" )
				.toString() );
	}

	public PillagerWolfEntity( EntityType< ? extends WolfEntity > type, World world ) {
		super( type, world );
		this.experienceValue = 4;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new SwimGoal( this ) );
		this.goalSelector.addGoal( 2, new MeleeAttackGoal( this, 1.0, true ) );
		this.goalSelector.addGoal( 4, new WaterAvoidingRandomWalkingGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 5, new LookAtGoal( this, PlayerEntity.class, 8.0f ) );
		this.goalSelector.addGoal( 5, new LookRandomlyGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, PlayerEntity.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, VillagerEntity.class, true ) );
		this.targetSelector.addGoal( 4, new NearestAttackableTargetGoal<>( this, IronGolemEntity.class, true ) );
	}

	/** Makes wolf unable to be tamed. */
	@Override
	public boolean func_233678_J__() {
		return true;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( AttributeHelper.Attributes.MAX_HEALTH, 12.0 )
			.createMutableAttribute( AttributeHelper.Attributes.MOVEMENT_SPEED, 0.3125 )
			.createMutableAttribute( AttributeHelper.Attributes.ATTACK_DAMAGE, 4.0 )
			.create();
	}
}

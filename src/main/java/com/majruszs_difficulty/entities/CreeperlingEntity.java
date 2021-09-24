package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/** Entity that is smaller version of Creeper. */
public class CreeperlingEntity extends Creeper {
	public static final EntityType< CreeperlingEntity > type;

	static {
		type = EntityType.Builder.of( CreeperlingEntity::new, MobCategory.MONSTER )
			.sized( 0.6f, 0.9f )
			.build( MajruszsDifficulty.getLocation( "creeperling" ).toString() );
	}

	public CreeperlingEntity( EntityType< ? extends CreeperlingEntity > type, Level world ) {
		super( type, world );
		this.explosionRadius = 2;
		this.xpReward = 3;
	}

	@Override
	public boolean isPowered() {
		return false; // creeperling can not be charged
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 0.75f;
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes().add( Attributes.MAX_HEALTH, 6.0 ).add( Attributes.MOVEMENT_SPEED, 0.35 ).build();
	}
}

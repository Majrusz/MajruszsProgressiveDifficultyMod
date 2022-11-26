package com.majruszsdifficulty.entities;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class BlackWidowEntity extends Spider {
	public static Supplier< EntityType< BlackWidowEntity > > createSupplier() {
		return ()->EntityType.Builder.of( BlackWidowEntity::new, MobCategory.MONSTER )
			.sized( 0.7f, 0.35f )
			.build( "black_widow" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 12.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 4.0 )
			.build();
	}

	public BlackWidowEntity( EntityType< ? extends BlackWidowEntity > type, Level world ) {
		super( type, world );
		this.xpReward = 3;
	}

	@Override
	protected float getStandingEyeHeight( Pose p_33799_, EntityDimensions p_33800_) {
		return 0.2f;
	}
}

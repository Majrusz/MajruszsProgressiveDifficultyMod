package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.World;

/** Entity that is smaller version of Creeper. */
public class CreeperlingEntity extends CreeperEntity {
	public static final EntityType< CreeperlingEntity > type;

	static {
		type = EntityType.Builder.create( CreeperlingEntity::new, EntityClassification.MONSTER )
			.size( 0.6f, 1.0f )
			.build( MajruszsDifficulty.getLocation( "creeperling" )
				.toString() );
	}

	public CreeperlingEntity( EntityType< ? extends CreeperEntity > type, World world ) {
		super( type, world );
		this.explosionRadius *= 0.7f;
	}

	@Override
	public boolean isCharged() {
		return false; // creeperling can not be charged
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 6.0 )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.35 )
			.create();
	}
}

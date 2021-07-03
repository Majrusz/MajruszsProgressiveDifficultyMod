package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.world.World;

public class ParasiteEntity extends SpiderEntity {
	public static final EntityType< ParasiteEntity > type;

	static {
		type = EntityType.Builder.create( ParasiteEntity::new, EntityClassification.MONSTER )
			.size( 0.7f, 0.5f )
			.build( MajruszsDifficulty.getLocation( "parasite" )
				.toString() );
	}

	public ParasiteEntity( EntityType< ? extends SpiderEntity > type, World world ) {
		super( type, world );
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntitySize size ) {
		return 0.45f;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 14.0 )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.35 )
			.create();
	}
}

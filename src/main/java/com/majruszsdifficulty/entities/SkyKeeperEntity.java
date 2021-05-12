package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.world.World;

/** Entity that is more powerful version of Phantom. */
public class SkyKeeperEntity extends PhantomEntity {
	public static final EntityType< SkyKeeperEntity > type;

	static {
		type = EntityType.Builder.create( SkyKeeperEntity::new, EntityClassification.MONSTER )
			.size( 0.9f, 0.5f )
			.build( MajruszsDifficulty.getLocation( "sky_keeper" )
				.toString() );
	}

	public SkyKeeperEntity( EntityType< ? extends PhantomEntity > type, World world ) {
		super( type, world );
		this.experienceValue = 7;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 30.0 )
			.createMutableAttribute( Attributes.ATTACK_DAMAGE, 5.0 )
			.create();
	}

	@Override
	public boolean isImmuneToFire() {
		return true;
	}
}

package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SkyKeeperEntity extends PhantomEntity {
	public static final EntityType< SkyKeeperEntity > type;

	static {
		type = EntityType.Builder.create( SkyKeeperEntity::new, EntityClassification.MONSTER )
			.size( 0.9f, 0.5f )
			.build( new ResourceLocation( MajruszsDifficulty.MOD_ID, "sky_keeper" ).toString() );
	}

	public SkyKeeperEntity( EntityType< ? extends PhantomEntity > type, World world ) {
		super( type, world );
		this.experienceValue = 7;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( AttributeHelper.Attributes.MAX_HEALTH, 26.0D )
			.createMutableAttribute( AttributeHelper.Attributes.ATTACK_DAMAGE, 12.0D )
			.create();
	}
}

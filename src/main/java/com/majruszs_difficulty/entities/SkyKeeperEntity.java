package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

/** Entity that is more powerful version of Phantom. */
public class SkyKeeperEntity extends Phantom {
	public static final EntityType< SkyKeeperEntity > type;

	static {
		type = EntityType.Builder.of( SkyKeeperEntity::new, MobCategory.MONSTER )
			.sized( 0.9f, 0.5f )
			.build( MajruszsDifficulty.getLocation( "sky_keeper" ).toString() );
	}

	public SkyKeeperEntity( EntityType< ? extends Phantom > type, Level world ) {
		super( type, world );
		this.xpReward = 7;
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes().add( Attributes.MAX_HEALTH, 30.0 ).add( Attributes.ATTACK_DAMAGE, 5.0 ).build();
	}
}

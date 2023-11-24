package com.majruszsdifficulty.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Illusioner extends net.minecraft.world.entity.monster.Illusioner {
	public static EntityType< Illusioner > createEntityType() {
		return EntityType.Builder.of( Illusioner::new, MobCategory.MONSTER )
			.sized( 0.6f, 1.95f )
			.clientTrackingRange( 8 )
			.build( "illusioner" );
	}

	public Illusioner( EntityType< ? extends Illusioner > entityType, Level level ) {
		super( entityType, level );
	}

	@Override
	public Vec3[] getIllusionOffsets( float ticks ) {
		Vec3[] offsets = super.getIllusionOffsets( ticks );
		Vec3[] newOffsets = new Vec3[ offsets.length + 1 ];
		System.arraycopy( offsets, 0, newOffsets, 0, offsets.length );
		newOffsets[ offsets.length ] = Vec3.ZERO;

		return newOffsets;
	}
}

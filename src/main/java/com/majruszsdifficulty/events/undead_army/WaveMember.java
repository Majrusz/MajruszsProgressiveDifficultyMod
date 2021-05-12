package com.majruszsdifficulty.events.undead_army;

import com.majruszsdifficulty.entities.EliteSkeletonEntity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Arrays;

/** Amount of entities in waves. */
public enum WaveMember implements IExtensibleEnum {
	ZOMBIE( EntityType.ZOMBIE, 5, 4, 3, 2, 2 ), HUSK( EntityType.HUSK, 1, 1, 2, 4, 5 ), SKELETON(
		EntityType.SKELETON, 3, 3, 2, 2, 2 ), STRAY( EntityType.STRAY, 1, 1, 2, 3, 4 ), ELITE_SKELETON( EliteSkeletonEntity.type, 1, 2, 3, 5, 6 );

	public final EntityType< ? > type;
	public final int[] waveCounts;

	WaveMember( EntityType< ? > type, int... waveCounts ) {
		this.type = type;
		this.waveCounts = waveCounts;
	}

	public static WaveMember create( String name, EntityType< ? > type, int[] waveCounts ) {
		throw new IllegalStateException( "Enum not extended" + name + type + Arrays.toString( waveCounts ) ); // weird but required
	}
}

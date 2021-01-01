package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.events.UndeadArmy;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Arrays;

public enum WaveMember implements IExtensibleEnum {
	ZOMBIE( EntityType.ZOMBIE, new int[]{ 5, 4, 3, 2, 2 } ),
	HUSK( EntityType.HUSK, new int[]{ 1, 1, 2, 3, 4 } ),
	GIANT( GiantEntity.type, new int[]{ 0, 0, 0, 1, 2 } ),
	SKELETON( EntityType.SKELETON, new int[]{ 3, 3, 2, 2, 2 } ),
	STRAY( EntityType.STRAY, new int[]{ 1, 1, 1, 2, 3 } ),
	ELITE_SKELETON( EliteSkeletonEntity.type, new int[]{ 1, 2, 3, 4, 5 } );

	public final EntityType< ? > type;
	public final int[] waveCounts;

	WaveMember( EntityType< ? > type, int[] waveCounts ) {
		this.type = type;
		this.waveCounts = waveCounts;
	}

	public static WaveMember create( String name, EntityType< ? > type, int[] waveCounts ) {
		throw new IllegalStateException( "Enum not extended" + name + type + Arrays.toString( waveCounts ) ); // weird but required
	}
}

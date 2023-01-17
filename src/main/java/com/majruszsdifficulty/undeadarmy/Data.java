package com.majruszsdifficulty.undeadarmy;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class Data {
	public Data( BlockPos positionToAttack, Direction direction ) {

	}

	public Data( CompoundTag nbt ) {

	}

	public CompoundTag write( CompoundTag nbt ) {
		return nbt;
	}
}

package com.majruszsdifficulty.undeadarmy;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class UndeadArmy {
	final ProgressIndicator progressIndicator = new ProgressIndicator();
	final ServerLevel level;
	final Data data;

	public UndeadArmy( ServerLevel level, BlockPos positionToAttack, Direction direction ) {
		this.level = level;
		this.data = new Data( positionToAttack, direction );
	}

	public UndeadArmy( ServerLevel level, CompoundTag nbt ) {
		this.level = level;
		this.data = new Data( nbt );
	}

	public CompoundTag write( CompoundTag nbt ) {
		return this.data.write( nbt );
	}
}

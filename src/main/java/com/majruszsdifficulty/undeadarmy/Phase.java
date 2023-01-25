package com.majruszsdifficulty.undeadarmy;

import net.minecraft.nbt.CompoundTag;

enum Phase {
	CREATED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, FINISHED;

	public void write( CompoundTag nbt ) {
		nbt.putString( Phase.class.getName(), this.toString() );
	}

	public static Phase read( CompoundTag nbt ) {
		String name = nbt.getString( Phase.class.getName() );
		for( Phase phase : Phase.values() ) {
			if( name.equalsIgnoreCase( phase.name() ) ) {
				return phase;
			}
		}

		return CREATED;
	}
}

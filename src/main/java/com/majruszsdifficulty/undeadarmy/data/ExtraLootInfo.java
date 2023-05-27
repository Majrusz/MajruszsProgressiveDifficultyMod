package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;
import net.minecraft.world.entity.Entity;

public class ExtraLootInfo extends SerializableStructure {
	public boolean hasExtraLoot = false;

	public static void addExtraLootTag( Entity entity ) {
		ExtraLootInfo extraLootInfo = new ExtraLootInfo();
		extraLootInfo.hasExtraLoot = true;
		extraLootInfo.write( entity.getPersistentData() );
	}

	public static boolean hasExtraLootTag( Entity entity ) {
		ExtraLootInfo extraLootInfo = new ExtraLootInfo();
		extraLootInfo.read( entity.getPersistentData() );

		return extraLootInfo.hasExtraLoot;
	}

	public ExtraLootInfo() {
		super( "UndeadArmy" );

		this.define( "has_extra_loot", ()->this.hasExtraLoot, x->this.hasExtraLoot = x );
	}
}

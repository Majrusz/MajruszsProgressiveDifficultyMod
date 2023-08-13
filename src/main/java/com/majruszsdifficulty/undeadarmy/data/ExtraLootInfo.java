package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import net.minecraft.world.entity.Entity;

public class ExtraLootInfo extends SerializableStructure {
	public boolean hasExtraLoot = false;

	public static void addExtraLootTag( Entity entity ) {
		SerializableHelper.modify( ExtraLootInfo::new, entity.getPersistentData(), info->info.hasExtraLoot = true );
	}

	public static boolean hasExtraLootTag( Entity entity ) {
		return SerializableHelper.read( ExtraLootInfo::new, entity.getPersistentData() ).hasExtraLoot;
	}

	public ExtraLootInfo() {
		super( "UndeadArmy" );

		this.defineBoolean( "has_extra_loot", ()->this.hasExtraLoot, x->this.hasExtraLoot = x );
	}
}

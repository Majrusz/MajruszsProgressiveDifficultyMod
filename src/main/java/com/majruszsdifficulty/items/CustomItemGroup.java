package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Custom creative mode item tab. */
public class CustomItemGroup extends CreativeModeTab {
	public CustomItemGroup( String label ) {
		super( label );
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack( Instances.BATTLE_STANDARD_ITEM );
	}
}

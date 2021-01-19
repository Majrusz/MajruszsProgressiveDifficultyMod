package com.majruszs_difficulty;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/** Custom creative mode item tab. */
public class CustomItemGroup extends ItemGroup {
	public CustomItemGroup( String label ) {
		super( label );
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack( Instances.Miscellaneous.BATTLE_STANDARD_ITEM );
	}
}

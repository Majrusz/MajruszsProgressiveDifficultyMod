package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/** Custom creative mode item tab. */
public class CustomItemGroup extends ItemGroup {
	public CustomItemGroup( String label ) {
		super( label );
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack( Instances.BATTLE_STANDARD_ITEM );
	}
}

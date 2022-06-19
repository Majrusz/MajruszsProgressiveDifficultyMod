package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CustomItemGroup extends CreativeModeTab {
	public CustomItemGroup( String label ) {
		super( label );
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack( Registries.BATTLE_STANDARD.get() );
	}
}

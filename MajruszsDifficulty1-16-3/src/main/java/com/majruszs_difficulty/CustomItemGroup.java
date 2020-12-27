package com.majruszs_difficulty;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CustomItemGroup extends ItemGroup {
	public CustomItemGroup( String label ) {
		super( label );
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack( RegistryHandler.UNDEAD_BATTLE_STANDARD.get() );
	}
}

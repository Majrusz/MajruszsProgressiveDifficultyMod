package com.majruszsdifficulty;

import com.majruszsdifficulty.items.CreativeModeTabs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod( MajruszsDifficulty.MOD_ID )
public class Initializer {
	public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab( "majruszsdifficulty.primary" ) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack( MajruszsDifficulty.UNDEAD_BATTLE_STANDARD_ITEM.get() );
		}

		@Override
		public void fillItemList( NonNullList< ItemStack > stacks ) {
			CreativeModeTabs.definePrimaryItems( stacks::add );
		}
	};

	public Initializer() {
		MajruszsDifficulty.HELPER.register();
		MinecraftForge.EVENT_BUS.register( this );
	}
}

package com.majruszsdifficulty.items;

import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class CreativeModeTabs {
	private static final Component PRIMARY = TextHelper.translatable( "itemGroup.majruszsdifficulty.primary" );

	public static Supplier< CreativeModeTab > primary() {
		return ()->CreativeModeTab.builder( CreativeModeTab.Row.TOP, 0 )
			.title( PRIMARY )
			.displayItems( CreativeModeTabs::definePrimaryItems )
			.icon( ()->new ItemStack( MajruszsDifficulty.FRAGILE_END_STONE.get() ) )
			.build();
	}

	private static void definePrimaryItems( CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output ) {
		Stream.of(
			MajruszsDifficulty.ENDERIUM_BLOCK,
			MajruszsDifficulty.ENDERIUM_SHARD_ORE,
			MajruszsDifficulty.FRAGILE_END_STONE,
			MajruszsDifficulty.INFESTED_END_STONE,
			MajruszsDifficulty.CERBERUS_SPAWN_EGG,
			MajruszsDifficulty.CREEPERLING_SPAWN_EGG,
			MajruszsDifficulty.CURSED_ARMOR_SPAWN_EGG,
			MajruszsDifficulty.GIANT_SPAWN_EGG,
			MajruszsDifficulty.TANK_SPAWN_EGG
		).map( item->new ItemStack( item.get() ) ).forEach( output::accept );
	}
}

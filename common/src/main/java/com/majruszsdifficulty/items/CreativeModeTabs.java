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
			.icon( ()->new ItemStack( MajruszsDifficulty.Items.FRAGILE_END_STONE.get() ) )
			.build();
	}

	private static void definePrimaryItems( CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output ) {
		Stream.of(
			MajruszsDifficulty.Items.INFERNAL_SPONGE,
			MajruszsDifficulty.Items.SOAKED_INFERNAL_SPONGE,
			MajruszsDifficulty.Items.ENDERIUM_BLOCK,
			MajruszsDifficulty.Items.ENDERIUM_SHARD_ORE,
			MajruszsDifficulty.Items.FRAGILE_END_STONE,
			MajruszsDifficulty.Items.INFESTED_END_STONE,
			MajruszsDifficulty.Items.BANDAGE,
			MajruszsDifficulty.Items.GOLDEN_BANDAGE,
			MajruszsDifficulty.Items.CLOTH,
			MajruszsDifficulty.Items.CERBERUS_FANG,
			MajruszsDifficulty.Items.ENDERIUM_INGOT,
			MajruszsDifficulty.Items.ENDERIUM_SHARD,
			MajruszsDifficulty.Items.CERBERUS_SPAWN_EGG,
			MajruszsDifficulty.Items.CREEPERLING_SPAWN_EGG,
			MajruszsDifficulty.Items.CURSED_ARMOR_SPAWN_EGG,
			MajruszsDifficulty.Items.GIANT_SPAWN_EGG,
			MajruszsDifficulty.Items.TANK_SPAWN_EGG
		).map( item->new ItemStack( item.get() ) ).forEach( output::accept );
	}
}

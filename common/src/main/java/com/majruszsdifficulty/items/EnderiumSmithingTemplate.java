package com.majruszsdifficulty.items;

import com.majruszlibrary.events.OnLootGenerated;
import com.majruszlibrary.item.LootHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public class EnderiumSmithingTemplate extends SmithingTemplateItem {
	private static final ResourceLocation ID = MajruszsDifficulty.HELPER.getLocation( "gameplay/enderium_upgrade_smithing_template" );

	static {
		OnLootGenerated.listen( EnderiumSmithingTemplate::addToChest )
			.addCondition( data->data.lootId.equals( BuiltInLootTables.END_CITY_TREASURE ) );
	}

	public EnderiumSmithingTemplate() {
		super(
			Component.translatable( "majruszsdifficulty.smithing.enderium.applies_to" ).withStyle( ChatFormatting.BLUE ),
			Component.translatable( "majruszsdifficulty.smithing.enderium.ingredients" ).withStyle( ChatFormatting.BLUE ),
			Component.translatable( "majruszsdifficulty.smithing.enderium.upgrade" ).withStyle( ChatFormatting.GRAY ),
			Component.translatable( "majruszsdifficulty.smithing.enderium.base_slot" ).withStyle( ChatFormatting.GRAY ),
			Component.translatable( "majruszsdifficulty.smithing.enderium.additions_slot" ).withStyle( ChatFormatting.GRAY ),
			List.of(
				new ResourceLocation( "item/empty_armor_slot_helmet" ),
				new ResourceLocation( "item/empty_slot_sword" ),
				new ResourceLocation( "item/empty_armor_slot_chestplate" ),
				new ResourceLocation( "item/empty_slot_pickaxe" ),
				new ResourceLocation( "item/empty_armor_slot_leggings" ),
				new ResourceLocation( "item/empty_slot_axe" ),
				new ResourceLocation( "item/empty_armor_slot_boots" ),
				new ResourceLocation( "item/empty_slot_hoe" ),
				new ResourceLocation( "item/empty_slot_shovel" )
			),
			List.of(
				new ResourceLocation( "item/empty_slot_ingot" )
			)
		);
	}

	private static void addToChest( OnLootGenerated data ) {
		LootHelper.getLootTable( ID )
			.getRandomItems( LootHelper.toGiftParams( data.entity ) ) // tochestparams
			.forEach( data.generatedLoot::add );
	}
}

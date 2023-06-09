package com.majruszsdifficulty.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class EnderiumSmithingTemplateItem extends SmithingTemplateItem {
	public EnderiumSmithingTemplateItem() {
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
}

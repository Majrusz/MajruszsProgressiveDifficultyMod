package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnLoot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

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

	@AutoInstance
	public static class AddToEndCity {
		static final ResourceLocation ID = Registries.getLocation( "gameplay/enderium_upgrade_smithing_template" );

		public AddToEndCity() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
				.name( "EnderiumUpgradeSmithingTemplate" )
				.comment( "Determines where Enderium Upgrade can be found." );

			OnLoot.listen( this::addToChest )
				.addCondition( Condition.isServer() )
				.addCondition( OnLoot.is( BuiltInLootTables.END_CITY_TREASURE ) )
				.insertTo( group );
		}

		private void addToChest( OnLoot.Data data ) {
			data.addAsChestLoot( ID );
		}
	}
}

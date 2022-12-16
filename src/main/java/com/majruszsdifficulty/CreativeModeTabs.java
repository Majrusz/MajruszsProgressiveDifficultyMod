package com.majruszsdifficulty;

import com.majruszsdifficulty.items.UndeadArmorItem;
import com.mlib.annotations.AutoInstance;
import com.mlib.items.CreativeModeTabHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;
import java.util.stream.Stream;

@AutoInstance
public class CreativeModeTabs {
	public CreativeModeTabs() {
		var primary = CreativeModeTabHelper.newTab( FMLJavaModLoadingContext.get(), Registries.getLocation( "primary" ) );
		primary.accept( builder->builder.title( Component.translatable( "itemGroup.majruszsdifficulty.primary" ) )
			.icon( ()->new ItemStack( Registries.BATTLE_STANDARD.get() ) )
			.displayItems( this::definePrimaryItems ) );

		var treasureBags = CreativeModeTabHelper.newTab( FMLJavaModLoadingContext.get(), Registries.getLocation( "treasure_bags" ) );
		treasureBags.accept( builder->builder.title( Component.translatable( "itemGroup.majruszsdifficulty.treasure_bags" ) )
			.displayItems( this::defineTreasureBagItems )
			.withTabFactory( TreasureBag::new ) );
	}

	private void definePrimaryItems( FeatureFlagSet flagSet, CreativeModeTab.Output output, boolean hasPermissions ) {
		Stream.of(
			new ItemStack( Registries.BANDAGE.get() ),
			new ItemStack( Registries.GOLDEN_BANDAGE.get() ),

			new ItemStack( Registries.CLOTH.get() ),
			new ItemStack( Registries.BATTLE_STANDARD.get() ),
			UndeadArmorItem.constructItem( UndeadArmorItem.HELMET_ID ),
			UndeadArmorItem.constructItem( UndeadArmorItem.CHESTPLATE_ID ),
			UndeadArmorItem.constructItem( UndeadArmorItem.LEGGINGS_ID ),
			UndeadArmorItem.constructItem( UndeadArmorItem.BOOTS_ID ),

			new ItemStack( Registries.INFESTED_END_STONE_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_SHARD_ORE_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_BLOCK_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_SHARD.get() ),
			new ItemStack( Registries.ENDERIUM_INGOT.get() ),
			new ItemStack( Registries.ENDERIUM_HELMET.get() ),
			new ItemStack( Registries.ENDERIUM_CHESTPLATE.get() ),
			new ItemStack( Registries.ENDERIUM_LEGGINGS.get() ),
			new ItemStack( Registries.ENDERIUM_BOOTS.get() ),
			new ItemStack( Registries.ENDERIUM_SWORD.get() ),
			new ItemStack( Registries.ENDERIUM_PICKAXE.get() ),
			new ItemStack( Registries.ENDERIUM_AXE.get() ),
			new ItemStack( Registries.ENDERIUM_SHOVEL.get() ),
			new ItemStack( Registries.ENDERIUM_HOE.get() ),

			new ItemStack( Registries.ILLUSIONER_SPAWN_EGG.get() ),
			new ItemStack( Registries.CREEPERLING_SPAWN_EGG.get() ),
			new ItemStack( Registries.TANK_SPAWN_EGG.get() ),
			new ItemStack( Registries.CURSED_ARMOR_SPAWN_EGG.get() )
		).forEach( output::accept );
	}

	private void defineTreasureBagItems( FeatureFlagSet flagSet, CreativeModeTab.Output output, boolean hasPermissions ) {
		Stream.of(
			new ItemStack( Registries.FISHING_TREASURE_BAG.get() ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Angler Treasure Bag Unique Item #1" ) ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Angler Treasure Bag Unique Item #2" ) ),

			new ItemStack( Registries.UNDEAD_ARMY_TREASURE_BAG.get() ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Undead Army Treasure Bag Unique Item #1" ) ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Undead Army Treasure Bag Unique Item #2" ) ),

			new ItemStack( Registries.PILLAGER_TREASURE_BAG.get() ),
			new ItemStack( Registries.RECALL_POTION.get() ),
			new ItemStack( Registries.BAD_OMEN_POTION.get() ),

			new ItemStack( Registries.ELDER_GUARDIAN_TREASURE_BAG.get() ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Elder Guardian Treasure Bag Unique Item #1" ) ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Elder Guardian Treasure Bag Unique Item #2" ) ),

			new ItemStack( Registries.WARDEN_TREASURE_BAG.get() ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Warden Treasure Bag Unique Item #1" ) ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Warden Treasure Bag Unique Item #2" ) ),

			new ItemStack( Registries.WITHER_TREASURE_BAG.get() ),
			new ItemStack( Registries.WITHER_SWORD.get() ),
			new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Wither Treasure Bag Unique Item #2" ) ),

			new ItemStack( Registries.ENDER_DRAGON_TREASURE_BAG.get() ),
			new ItemStack( Registries.ENDERIUM_SHARD_LOCATOR.get() ),
			new ItemStack( Registries.ENDER_POUCH.get() )
		).forEach( output::accept );
	}

	private static class TreasureBag extends CreativeModeTab {
		final Supplier< ItemStack > currentIcon;

		protected TreasureBag( Builder builder ) {
			super( builder );

			this.currentIcon = CreativeModeTabHelper.buildMultiIcon( Stream.of(
				Registries.FISHING_TREASURE_BAG,
				Registries.UNDEAD_ARMY_TREASURE_BAG,
				Registries.PILLAGER_TREASURE_BAG,
				Registries.ELDER_GUARDIAN_TREASURE_BAG,
				Registries.WARDEN_TREASURE_BAG,
				Registries.WITHER_TREASURE_BAG,
				Registries.ENDER_DRAGON_TREASURE_BAG
			) );
		}

		@Override
		public ItemStack getIconItem() {
			return this.currentIcon.get();
		}
	}
}

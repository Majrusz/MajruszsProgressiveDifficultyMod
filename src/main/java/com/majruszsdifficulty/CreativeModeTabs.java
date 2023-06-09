package com.majruszsdifficulty;

import com.majruszsdifficulty.items.SoulJarItem;
import com.mlib.items.CreativeModeTabHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class CreativeModeTabs {
	public static Supplier< CreativeModeTab > primary() {
		return ()->CreativeModeTab.builder()
			.title( Component.translatable( "itemGroup.majruszsdifficulty.primary" ) )
			.icon( ()->new ItemStack( Registries.BATTLE_STANDARD.get() ) )
			.displayItems( CreativeModeTabs::definePrimaryItems )
			.build();
	}

	public static Supplier< CreativeModeTab > treasureBags() {
		return ()->CreativeModeTab.builder()
			.title( Component.translatable( "itemGroup.majruszsdifficulty.treasure_bags" ) )
			.displayItems( CreativeModeTabs::defineTreasureBagItems )
			.withTabFactory( TreasureBag::new )
			.build();
	}

	private static void definePrimaryItems( CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output ) {
		Stream.of(
			new ItemStack( Registries.BANDAGE.get() ),
			new ItemStack( Registries.GOLDEN_BANDAGE.get() ),

			new ItemStack( Registries.CLOTH.get() ),
			new ItemStack( Registries.BATTLE_STANDARD.get() ),
			new ItemStack( Registries.TATTERED_HELMET.get() ),
			new ItemStack( Registries.TATTERED_CHESTPLATE.get() ),
			new ItemStack( Registries.TATTERED_LEGGINGS.get() ),
			new ItemStack( Registries.TATTERED_BOOTS.get() ),
			new ItemStack( Registries.CERBERUS_FANG.get() ),

			new ItemStack( Registries.INFESTED_END_STONE_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_SHARD_ORE_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_BLOCK_ITEM.get() ),
			new ItemStack( Registries.ENDERIUM_SMITHING_TEMPLATE.get() ),
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
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION_STRONG.get() )
		).forEach( output::accept );

		Stream.of(
			Items.TIPPED_ARROW,
			Items.POTION,
			Items.SPLASH_POTION,
			Items.LINGERING_POTION
		).forEach( item->Stream.of(
			Registries.WITHER_POTION,
			Registries.WITHER_POTION_LONG,
			Registries.WITHER_POTION_STRONG
		).forEach( potion->PotionUtils.setPotion( new ItemStack( item ), potion.get() ) ) );

		Stream.of(
			new ItemStack( Registries.ILLUSIONER_SPAWN_EGG.get() ),
			new ItemStack( Registries.CREEPERLING_SPAWN_EGG.get() ),
			new ItemStack( Registries.TANK_SPAWN_EGG.get() ),
			new ItemStack( Registries.CURSED_ARMOR_SPAWN_EGG.get() ),
			new ItemStack( Registries.CERBERUS_SPAWN_EGG.get() ),
			new ItemStack( Registries.BLACK_WIDOW_SPAWN_EGG.get() )
		).forEach( output::accept );
	}

	private static ItemStack newItemStackWithPotion( Item item, Potion potion ) {
		return PotionUtils.setPotion( new ItemStack( item ), potion );
	}

	private static void defineTreasureBagItems( CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output ) {
		Stream.of(
			new ItemStack( Registries.FISHING_TREASURE_BAG.get() ),
			notImplemented(),
			notImplemented(),

			new ItemStack( Registries.UNDEAD_ARMY_TREASURE_BAG.get() ),
			SoulJarItem.randomItemStack( 3 ),
			notImplemented(),

			new ItemStack( Registries.PILLAGER_TREASURE_BAG.get() ),
			new ItemStack( Registries.RECALL_POTION.get() ),
			new ItemStack( Registries.BAD_OMEN_POTION.get() ),

			new ItemStack( Registries.ELDER_GUARDIAN_TREASURE_BAG.get() ),
			notImplemented(),
			notImplemented(),

			new ItemStack( Registries.WARDEN_TREASURE_BAG.get() ),
			notImplemented(),
			notImplemented(),

			new ItemStack( Registries.WITHER_TREASURE_BAG.get() ),
			new ItemStack( Registries.WITHER_SWORD.get() ),
			notImplemented(),

			new ItemStack( Registries.ENDER_DRAGON_TREASURE_BAG.get() ),
			new ItemStack( Registries.ENDERIUM_SHARD_LOCATOR.get() ),
			new ItemStack( Registries.ENDER_POUCH.get() )
		).forEach( output::accept );
	}

	static int counter = 0;

	private static ItemStack notImplemented() {
		ItemStack itemStack = new ItemStack( Items.DIRT ).setHoverName( Component.translatable( "Coming soon..." ) );
		itemStack.getOrCreateTagElement( String.format( "dirt_%d", ++counter ) ); // items must be unique, so well...

		return itemStack;
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

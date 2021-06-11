package com.majruszs_difficulty.events;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.items.InventoryItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Adds certain inventory items to villager trades. */
@Mod.EventBusSubscriber
public class AddInventoryItemsToVillagerTrades {
	private static final List< TradeRegistry > TRADE_REGISTRIES = new ArrayList<>();
	static {
		TRADE_REGISTRIES.add( new TradeRegistry( VillagerProfession.BUTCHER, Instances.IDOL_OF_FERTILITY_ITEM, 2 ) );
		TRADE_REGISTRIES.add( new TradeRegistry( VillagerProfession.FARMER, Instances.GIANT_SEED_ITEM, 5 ) );
		TRADE_REGISTRIES.add( new TradeRegistry( VillagerProfession.FISHERMAN, Instances.FISHERMAN_EMBLEM_ITEM, 2 ) );
		TRADE_REGISTRIES.add( new TradeRegistry( VillagerProfession.MASON, Instances.LUCKY_ROCK_ITEM, 2 ) );
		TRADE_REGISTRIES.add( new TradeRegistry( VillagerProfession.SHEPHERD, Instances.TAMING_CERTIFICATE_ITEM, 2 ) );
	}

	@SubscribeEvent
	public static void addTrades( VillagerTradesEvent event ) {
		for( TradeRegistry tradeRegistry : TRADE_REGISTRIES )
			tradeRegistry.registerIfValidProfession( event );
	}

	/** Class for easier trade registry. */
	static class TradeRegistry {
		private final VillagerProfession profession;
		private final InventoryItem item;
		private final int tradeTier;

		public TradeRegistry( VillagerProfession profession, InventoryItem inventoryItem, int tradeTier ) {
			this.profession = profession;
			this.item = inventoryItem;
			this.tradeTier = tradeTier;
		}

		/** Adds new inventory item trade if profession is valid. */
		public void registerIfValidProfession( VillagerTradesEvent event ) {
			if( !this.profession.equals( event.getType() ) )
				return;

			Int2ObjectMap< List< ITrade > > tradeLevels = event.getTrades();
			List< ITrade > trades = tradeLevels.get( this.tradeTier );
			trades.add( new InventoryItemTrade( this.item ) );
		}
	}

	/** Villager trade with inventory item only. */
	static class InventoryItemTrade implements VillagerTrades.ITrade {
		private final InventoryItem tradeItem;

		public InventoryItemTrade( InventoryItem tradeItem ) {
			this.tradeItem = tradeItem;
		}

		@Override
		public MerchantOffer getOffer( Entity trader, Random rand ) {
			return new InventoryItemOffer( this.tradeItem );
		}
	}

	/** Villager trade offer with inventory item. */
	static class InventoryItemOffer extends MerchantOffer {
		protected InventoryItem inventoryItem;

		public InventoryItemOffer( InventoryItem inventoryItem ) {
			super( new ItemStack( inventoryItem, 1 ), new ItemStack( Items.EMERALD, 17 ), 3, 40, 0.05f );

			this.inventoryItem = inventoryItem;
		}

		@Override
		public boolean matches( ItemStack itemStack1, ItemStack itemStack2 ) {
			return this.inventoryItem.equals( itemStack1.getItem() ) || this.inventoryItem.equals( itemStack2.getItem() );
		}
	}
}

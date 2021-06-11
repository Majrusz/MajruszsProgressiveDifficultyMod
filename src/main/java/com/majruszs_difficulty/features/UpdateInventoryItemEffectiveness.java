package com.majruszs_difficulty.features;

import com.majruszs_difficulty.items.InventoryItem;
import com.mlib.events.AnyLootModificationEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Updates effectiveness for inventory items acquired from crafting, loot etc. */
@Mod.EventBusSubscriber
public class UpdateInventoryItemEffectiveness {
	@SubscribeEvent
	public static void onCrafting( PlayerEvent.ItemCraftedEvent event ) {
		updateEffectiveness( event.getCrafting() );
	}

	@SubscribeEvent
	public static void onAnyLoot( AnyLootModificationEvent event ) {
		for( ItemStack itemStack : event.generatedLoot )
			updateEffectiveness( itemStack );
	}

	/** Sets random effectiveness if item stack does not have any. */
	private static void updateEffectiveness( ItemStack itemStack ) {
		if( !( itemStack.getItem() instanceof InventoryItem ) )
			return;

		InventoryItem inventoryItem = ( InventoryItem )itemStack.getItem();
		if( !inventoryItem.hasEffectivenessTag( itemStack ) )
			inventoryItem.setRandomEffectiveness( itemStack );
	}
}

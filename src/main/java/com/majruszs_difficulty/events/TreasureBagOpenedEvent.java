package com.majruszs_difficulty.events;

import com.majruszs_difficulty.items.TreasureBagItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import java.util.List;

/** Event called when the player opens any treasure bag. */
public class TreasureBagOpenedEvent extends PlayerEvent implements IModBusEvent {
	public final TreasureBagItem treasureBagItem;
	public final List< ItemStack > generatedLoot;

	public TreasureBagOpenedEvent( PlayerEntity player, TreasureBagItem item, List< ItemStack > generatedLoot ) {
		super( player );
		this.treasureBagItem = item;
		this.generatedLoot = generatedLoot;
	}
}

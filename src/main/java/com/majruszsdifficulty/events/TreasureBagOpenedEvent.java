package com.majruszsdifficulty.events;

import com.majruszsdifficulty.items.TreasureBagItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;

/** Event called when the player opens any treasure bag. */
public class TreasureBagOpenedEvent extends PlayerEvent implements IModBusEvent {
	public final TreasureBagItem treasureBagItem;
	public final List< ItemStack > generatedLoot;

	public TreasureBagOpenedEvent( Player player, TreasureBagItem item, List< ItemStack > generatedLoot ) {
		super( player );
		this.treasureBagItem = item;
		this.generatedLoot = generatedLoot;
	}
}

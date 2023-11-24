package com.majruszsdifficulty.treasurebag.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.items.TreasureBag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class OnTreasureBagOpened {
	public final Player player;
	public final TreasureBag treasureBag;
	public final List< ItemStack > loot;

	public static Event< OnTreasureBagOpened > listen( Consumer< OnTreasureBagOpened > consumer ) {
		return Events.get( OnTreasureBagOpened.class ).add( consumer );
	}

	public OnTreasureBagOpened( Player player, TreasureBag treasureBag, List< ItemStack > loot ) {
		this.player = player;
		this.treasureBag = treasureBag;
		this.loot = loot;
	}
}

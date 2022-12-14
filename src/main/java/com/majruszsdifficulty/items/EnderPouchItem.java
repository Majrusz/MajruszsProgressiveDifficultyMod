package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnderPouchItem extends Item {
	public EnderPouchItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );

		new OnPlayerInteract.Context( this::openEnderChest )
			.addCondition( data->data.itemStack.getItem() instanceof EnderPouchItem );
	}

	private void openEnderChest( OnPlayerInteract.Data data ) {
		data.player.openMenu( new SimpleMenuProvider( ( containerId, inventory, player )->ChestMenu.threeRows( containerId, inventory, player.getEnderChestInventory() ), this.getDescription() ) );
		data.player.awardStat( Stats.OPEN_ENDERCHEST );
		data.event.setCancellationResult( InteractionResult.SUCCESS );
	}
}

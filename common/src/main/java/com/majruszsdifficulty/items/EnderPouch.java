package com.majruszsdifficulty.items;

import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.events.OnPlayerInteracted;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnderPouch extends Item {
	static {
		OnPlayerInteracted.listen( EnderPouch::openEnderChest )
			.addCondition( data->data.itemStack.getItem() instanceof EnderPouch );
	}

	public EnderPouch() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	private static void openEnderChest( OnPlayerInteracted data ) {
		if( data.getLevel() instanceof ServerLevel level ) {
			SoundEmitter.of( SoundEvents.ENDERMAN_TELEPORT )
				.volume( SoundEmitter.randomized( 0.3f ) )
				.pitch( SoundEmitter.randomized( 0.8f ) )
				.position( data.player.position() )
				.emit( level );
		}

		Component component = data.itemStack.getItem().getDescription();
		data.player.openMenu( new SimpleMenuProvider( ( containerId, inventory, player )->ChestMenu.threeRows( containerId, inventory, player.getEnderChestInventory() ), component ) );
		data.player.awardStat( Stats.OPEN_ENDERCHEST );
		data.finish();
	}
}

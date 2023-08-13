package com.majruszsdifficulty.items;

import com.mlib.effects.SoundHandler;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.OnPlayerInteract;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnderPouchItem extends Item {
	public EnderPouchItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );

		OnPlayerInteract.listen( this::openEnderChest )
			.addCondition( Condition.predicate( data->data.itemStack.getItem() instanceof EnderPouchItem ) );
	}

	private void openEnderChest( OnPlayerInteract.Data data ) {
		data.player.openMenu( new SimpleMenuProvider( ( containerId, inventory, player )->ChestMenu.threeRows( containerId, inventory, player.getEnderChestInventory() ), this.getDescription() ) );
		data.player.awardStat( Stats.OPEN_ENDERCHEST );
		data.player.swing( data.hand );
		if( data.getLevel() instanceof ServerLevel level ) {
			SoundHandler.ENDERMAN_TELEPORT.play( level, data.player.position(), SoundHandler.randomized( 0.5f ), SoundHandler.randomized( 0.7f ) );
		}
		data.event.setCancellationResult( InteractionResult.SUCCESS );
	}
}

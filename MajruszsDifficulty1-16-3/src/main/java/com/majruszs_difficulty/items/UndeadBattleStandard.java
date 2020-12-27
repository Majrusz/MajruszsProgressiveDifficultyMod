package com.majruszs_difficulty.items;

import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.events.UndeadArmy;
import com.majruszs_difficulty.events.undead_army.UndeadArmyManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class UndeadBattleStandard extends Item {
	public UndeadBattleStandard() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( RegistryHandler.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote ) {
			if( RegistryHandler.undeadArmyManager.spawn( player, ( ServerWorld )world ) ) {
				if( !player.abilities.isCreativeMode )
					itemStack.shrink( 1 );
				player.addStat( Stats.ITEM_USED.get( this ) );
			} else
				player.sendStatusMessage( RegistryHandler.undeadArmyManager.getFailedMessage(), true );
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}
}

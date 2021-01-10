package com.majruszs_difficulty.events;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Awarding player with fisherman treasure bag after certain amount of fished items. */
@Mod.EventBusSubscriber
public class AwardTreasureBagForFishing {
	protected static final String fishingTag = "FishermanTreasureBagCounter";

	@SubscribeEvent
	public static void onFishing( ItemFishedEvent event ) {
		PlayerEntity player = event.getPlayer();

		if( !( player.world instanceof ServerWorld ) )
			return;

		CompoundNBT data = player.getPersistentData();
		int fishedItemsCounter = data.getInt( fishingTag ) + 1;
		data.putInt( fishingTag, fishedItemsCounter );

		if( fishedItemsCounter >= getRequiredItems() ) {
			data.putInt( fishingTag, fishedItemsCounter - getRequiredItems() );
			giveTreasureBagTo( player );
		}
	}

	/** Gives a treasure bag to specified player. */
	protected static void giveTreasureBagTo( PlayerEntity player ) {
		ItemStack treasureBag = new ItemStack( RegistryHandler.FISHING_TREASURE_BAG.get() );
		MajruszsHelper.giveItemStackToPlayer( treasureBag, player, ( ServerWorld )player.world );
	}

	/** Returns how many fish player must fished to get treasure bag. */
	protected static int getRequiredItems() {
		switch( GameState.getCurrentMode() ) {
			default:
				return Config.getInteger( Config.Values.FISHED_ITEMS_BAG_REQUIREMENT_NORMAL );
			case EXPERT:
				return Config.getInteger( Config.Values.FISHED_ITEMS_BAG_REQUIREMENT_EXPERT );
			case MASTER:
				return Config.getInteger( Config.Values.FISHED_ITEMS_BAG_REQUIREMENT_MASTER );
		}
	}
}

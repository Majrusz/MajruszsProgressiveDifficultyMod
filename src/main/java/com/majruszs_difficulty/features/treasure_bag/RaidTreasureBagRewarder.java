package com.majruszs_difficulty.features.treasure_bag;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.items.TreasureBagItem;
import com.mlib.items.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Awards the player with Raid Treasure Bag after defeating Pillager's raid. */
@Mod.EventBusSubscriber
public class RaidTreasureBagRewarder {
	private static final String RAID_TAG = "TreasureBagLastPillagerRaidID";

	@SubscribeEvent
	public static void onTick( TickEvent.PlayerTickEvent event ) {
		if( !( event.player.world instanceof ServerWorld ) || event.phase.equals( TickEvent.Phase.START ) )
			return;

		PlayerEntity player = event.player;
		ServerWorld world = ( ServerWorld )player.world;
		Raid pillagerRaid = world.findRaid( player.getPosition() );
		if( pillagerRaid == null || !pillagerRaid.isVictory() || !player.isPotionActive( Effects.HERO_OF_THE_VILLAGE ) )
			return;

		if( pillagerRaid.getId() != getLastRaidID( player ) ) {
			setLastRaidID( player, pillagerRaid.getId() );

			TreasureBagItem treasureBagItem = Instances.PILLAGER_TREASURE_BAG;
			if( treasureBagItem.isAvailable() )
				ItemHelper.giveItemStackToPlayer( new ItemStack( treasureBagItem ), player, ( ServerWorld )player.world );
		}
	}

	/** Returns ID of last raid that rewarded the player with a Treasure Bag. */
	private static int getLastRaidID( PlayerEntity player ) {
		return player.getPersistentData().getInt( RAID_TAG );
	}

	/** Updates ID of last raid that rewarded the player with a Treasure Bag. */
	public static void setLastRaidID( PlayerEntity player, int id ) {
		CompoundNBT nbt = player.getPersistentData();
		nbt.putInt( RAID_TAG, id );

		player.writeAdditional( nbt );
	}
}

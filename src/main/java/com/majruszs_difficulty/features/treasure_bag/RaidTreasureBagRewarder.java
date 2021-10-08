package com.majruszs_difficulty.features.treasure_bag;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.items.TreasureBagItem;
import com.mlib.items.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Awards the player with Raid Treasure Bag after defeating Pillager's raid. */
@Mod.EventBusSubscriber
public class RaidTreasureBagRewarder {
	private static final String RAID_TAG = "TreasureBagLastPillagerRaidID";

	@SubscribeEvent
	public static void onTick( TickEvent.PlayerTickEvent event ) {
		if( !( event.player.level instanceof ServerLevel ) || event.phase.equals( TickEvent.Phase.START ) )
			return;

		Player player = event.player;
		ServerLevel world = ( ServerLevel )player.level;
		Raid pillagerRaid = world.getRaidAt( player.blockPosition() );
		if( pillagerRaid == null || !pillagerRaid.isVictory() || !player.hasEffect( MobEffects.HERO_OF_THE_VILLAGE ) )
			return;

		if( pillagerRaid.getId() != getLastRaidID( player ) ) {
			setLastRaidID( player, pillagerRaid.getId() );

			TreasureBagItem treasureBagItem = Instances.PILLAGER_TREASURE_BAG;
			if( treasureBagItem.isAvailable() )
				ItemHelper.giveItemStackToPlayer( new ItemStack( treasureBagItem ), player, ( ServerLevel )player.level );
		}
	}

	/** Returns ID of last raid that rewarded the player with a Treasure Bag. */
	private static int getLastRaidID( Player player ) {
		return player.getPersistentData().getInt( RAID_TAG );
	}

	/** Updates ID of last raid that rewarded the player with a Treasure Bag. */
	public static void setLastRaidID( Player player, int id ) {
		CompoundTag nbt = player.getPersistentData();
		nbt.putInt( RAID_TAG, id );

		player.save( nbt );
	}
}

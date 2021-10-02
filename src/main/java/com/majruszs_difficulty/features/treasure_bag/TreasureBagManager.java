package com.majruszs_difficulty.features.treasure_bag;

import com.majruszs_difficulty.items.TreasureBagItem;
import com.mlib.LevelHelper;
import com.mlib.MajruszLibrary;
import com.mlib.items.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Manager for easier handling Treasure Bags. */
public class TreasureBagManager {
	public static final String TREASURE_BAG_TAG = "TreasureBagPlayersToReward", PLAYER_TAG = "TreasureBagPlayerUUID";
	private static final List< Register > registers = new ArrayList<>();

	/**
	 Adding new treasure bag for given entity.

	 @param entityType  Entity that will drop treasure bag.
	 @param treasureBag Treasure Bag for each player.
	 @param replaceLoot Should treasure bag replace default loot? When it is true entity will only drop treasure bag.
	 */
	public static void addTreasureBagTo( EntityType< ? > entityType, TreasureBagItem treasureBag, boolean replaceLoot ) {
		registers.add( new Register( entityType, treasureBag, replaceLoot ) );
	}

	/** Checks whether entity type has any treasure bag. */
	public static boolean hasTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return true;

		return false;
	}

	/** Checks whether loot should be replaced depending on entity type. */
	public static boolean shouldReplaceLoot( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return register.shouldReplaceLoot;

		return false;
	}

	/** Returns treasure bag for given entity type. */
	@Nullable
	public static TreasureBagItem getTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return register.treasureBag;

		return null;
	}

	/** Reward all players that deal damage to given entity. */
	public static boolean rewardAllPlayers( LivingEntity killedEntity ) {
		CompoundTag data = killedEntity.getPersistentData();
		ServerLevel world = LevelHelper.getServerLevelFromEntity( killedEntity );

		if( data.contains( TREASURE_BAG_TAG ) && world != null ) {
			ListTag listNBT = data.getList( TREASURE_BAG_TAG, 10 );

			for( int i = 0; i < listNBT.size(); i++ ) {
				CompoundTag playerNBT = listNBT.getCompound( i );
				String playerUUID = playerNBT.getString( PLAYER_TAG );

				Player player = world.getPlayerByUUID( UUID.fromString( playerUUID ) );
				if( player != null )
					ItemHelper.giveItemStackToPlayer( new ItemStack( getTreasureBag( killedEntity.getType() ) ), player, world );
			}
			int amountOfPlayersRewarded = listNBT.size();
			listNBT.clear();

			return amountOfPlayersRewarded > 0;
		}

		return false;
	}

	/** Register representing Treasure Bag assigned to entity type. */
	static class Register {
		public final EntityType< ? > entityType;
		public final TreasureBagItem treasureBag;
		public final boolean shouldReplaceLoot;

		public Register( EntityType< ? > entityType, TreasureBagItem treasureBag, boolean replaceLoot ) {
			this.entityType = entityType;
			this.treasureBag = treasureBag;
			this.shouldReplaceLoot = replaceLoot;
		}
	}
}

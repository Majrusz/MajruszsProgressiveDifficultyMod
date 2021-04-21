package com.majruszs_difficulty.events.treasure_bag;

import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.items.TreasureBagItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Manager for easier handling Treasure Bags. */
public class TreasureBagManager {
	public static final String TREASURE_BAG_TAG = "TreasureBagPlayersToReward", PLAYER_TAG = "TreasureBagPlayerUUID";
	private static final List< Register > registers = new ArrayList<>();

	/** Adding new treasure bag for given entity.

	 @param entityType Entity that will drop treasure bag.
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
		CompoundNBT data = killedEntity.getPersistentData();
		ServerWorld world = MajruszsHelper.getServerWorldFromEntity( killedEntity );

		if( data.contains( TREASURE_BAG_TAG ) && world != null ) {
			ListNBT listNBT = data.getList( TREASURE_BAG_TAG, 10 );

			for( int i = 0; i < listNBT.size(); i++ ) {
				CompoundNBT playerNBT = listNBT.getCompound( i );
				String playerUUID = playerNBT.getString( PLAYER_TAG );

				PlayerEntity player = world.getPlayerByUuid( UUID.fromString( playerUUID ) );
				if( player == null )
					continue;

				MajruszsHelper.giveItemStackToPlayer( new ItemStack( getTreasureBag( killedEntity.getType() ) ), player, world );
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

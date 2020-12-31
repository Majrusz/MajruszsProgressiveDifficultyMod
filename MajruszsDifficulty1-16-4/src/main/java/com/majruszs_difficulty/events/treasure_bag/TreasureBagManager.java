package com.majruszs_difficulty.events.treasure_bag;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreasureBagManager {
	public static final String treasureBagTag = "PlayersToReward", playerTag = "UUID";
	private static final List< Register > registers = new ArrayList<>();

	public static void addTreasureBagTo( EntityType< ? > entityType, Item treasureBag, boolean replaceLoot ) {
		registers.add( new Register( entityType, treasureBag, replaceLoot ) );
	}

	public static boolean hasTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return true;

		return false;
	}

	public static boolean shouldReplaceLoot( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return register.shouldReplaceLoot;

		return false;
	}

	@Nullable
	public static Item getTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return register.treasureBag;

		return null;
	}

	public static boolean rewardAllPlayers( LivingEntity killedEntity ) {
		CompoundNBT data = killedEntity.getPersistentData();
		ServerWorld world = MajruszsHelper.getServerWorldFromEntity( killedEntity );

		if( data.contains( treasureBagTag ) && world != null ) {
			ListNBT listNBT = data.getList( treasureBagTag, 10 );

			for( int i = 0; i < listNBT.size(); i++ ) {
				CompoundNBT playerNBT = listNBT.getCompound( i );
				String playerUUID = playerNBT.getString( playerTag );

				PlayerEntity player = world.getPlayerByUuid( UUID.fromString( playerUUID ) );
				if( player == null )
					continue;

				MajruszsHelper.giveItemStackToPlayer( new ItemStack( getTreasureBag( killedEntity.getType() ) ), player, world );
			}

			return listNBT.size() > 0;
		}

		return false;
	}

	static class Register {
		public final EntityType< ? > entityType;
		public final Item treasureBag;
		public final boolean shouldReplaceLoot;

		public Register( EntityType< ? > entityType, Item treasureBag, boolean replaceLoot ) {
			this.entityType = entityType;
			this.treasureBag = treasureBag;
			this.shouldReplaceLoot = replaceLoot;
		}
	}
}

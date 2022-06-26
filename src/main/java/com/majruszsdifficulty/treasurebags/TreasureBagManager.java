package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.levels.LevelHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreasureBagManager {
	public static final String TREASURE_BAG_TAG = "TreasureBagPlayersToReward", PLAYER_TAG = "TreasureBagPlayerUUID";
	static final List< Register > REGISTERS = new ArrayList<>();

	public static void addTreasureBagTo( EntityType< ? > entityType, TreasureBagItem treasureBag, boolean replaceLoot ) {
		REGISTERS.add( new Register( entityType, treasureBag, replaceLoot ) );
	}

	public static boolean hasTreasureBag( EntityType< ? > entityType ) {
		return getRegisterFor( entityType ) != null;
	}

	public static boolean shouldReplaceLoot( EntityType< ? > entityType ) {
		Register register = getRegisterFor( entityType );
		return register != null && register.shouldReplaceLoot;
	}

	@Nullable
	public static TreasureBagItem getTreasureBag( EntityType< ? > entityType ) {
		Register register = getRegisterFor( entityType );
		return register != null ? register.treasureBag : null;
	}

	public static boolean rewardAllPlayers( LivingEntity killedEntity ) {
		CompoundTag data = killedEntity.getPersistentData();
		ServerLevel level = LevelHelper.getServerLevelFromEntity( killedEntity );

		if( data.contains( TREASURE_BAG_TAG ) && level != null ) {
			ListTag listNBT = data.getList( TREASURE_BAG_TAG, 10 );
			int amountOfPlayersRewarded = 0;
			for( int i = 0; i < listNBT.size(); i++ ) {
				Player player = getPlayerByUUID( level, listNBT.getCompound( i ) );
				if( player != null ) { // player could disconnect etc.
					ItemHelper.giveItemStackToPlayer( new ItemStack( getTreasureBag( killedEntity.getType() ) ), player, level );
					++amountOfPlayersRewarded;
				}
			}
			listNBT.clear();

			return amountOfPlayersRewarded > 0;
		}

		return false;
	}

	@Nullable
	private static Player getPlayerByUUID( ServerLevel level, CompoundTag tag ) {
		return level.getPlayerByUUID( UUID.fromString( tag.getString( PLAYER_TAG ) ) );
	}

	@Nullable
	private static Register getRegisterFor( EntityType< ? > entityType ) {
		for( Register register : REGISTERS )
			if( register.entityType.equals( entityType ) )
				return register;

		return null;
	}

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

	static class Handler extends GameModifier {
		public Handler() {
			super( GameModifier.TREASURE_BAG, "", "" );
		}

		@Override
		public void execute( Object object ) {

		}
	}
}

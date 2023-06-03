package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnItemFished;
import com.mlib.gamemodifiers.contexts.OnPlayerTick;
import com.mlib.items.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Responsible for awarding the player with Treasure Bags. */
@AutoInstance
public class TreasureBagManager {
	static final String PARTICIPANT_LIST_TAG = "TreasureBagPlayersToReward";
	static final String PLAYER_TAG = "TreasureBagPlayerUUID";
	static final List< Register > REGISTERS = new ArrayList<>();

	public static void addTreasureBagTo( EntityType< ? > entityType, TreasureBagItem treasureBag ) {
		REGISTERS.add( new Register( entityType, treasureBag ) );
	}

	@Nullable
	public static TreasureBagItem getTreasureBag( EntityType< ? > entityType ) {
		Register register = getRegisterFor( entityType );
		return register != null ? register.treasureBag : null;
	}

	public static boolean hasTreasureBag( EntityType< ? > entityType ) {
		return getRegisterFor( entityType ) != null;
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

	public TreasureBagManager() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.TREASURE_BAG )
			.addConfigs( TreasureBagItem.getConfigs() );

		OnDamaged.listen( this::addPlayerToParticipantList )
			.addCondition( Condition.predicate( data->data.attacker instanceof Player ) )
			.addCondition( Condition.predicate( data->hasTreasureBag( data.target.getType() ) ) )
			.insertTo( group );

		OnDeath.listen( this::rewardAllParticipants )
			.addCondition( Condition.predicate( data->hasTreasureBag( data.target.getType() ) ) )
			.addCondition( Condition.predicate( data->{
				TreasureBagItem treasureBag = getTreasureBag( data.target.getType() );
				return treasureBag != null && treasureBag.isEnabled();
			} ) ).insertTo( group );

		OnItemFished.listen( this::giveTreasureBagToAngler )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.predicate( data->{
				CompoundTag tag = data.player.getPersistentData();
				FishingData fishingData = new FishingData();
				fishingData.read( tag );
				fishingData.fishedItems = ( fishingData.fishedItems + 1 ) % TreasureBagItem.Fishing.REQUIRED_FISH_COUNT.getCurrentGameStageValue();
				fishingData.write( tag );

				return fishingData.fishedItems == 0;
			} ) ).addCondition( Condition.predicate( data->TreasureBagItem.Fishing.CONFIG.isEnabled() ) )
			.insertTo( group );

		OnPlayerTick.listen( this::giveTreasureBagToHero )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.< OnPlayerTick.Data > cooldown( 20, Dist.DEDICATED_SERVER ).configurable( false ) )
			.addCondition( Condition.predicate( data->{
				Raid raid = data.getServerLevel().getRaidAt( data.player.blockPosition() );
				if( raid == null || !raid.isVictory() || !data.player.hasEffect( MobEffects.HERO_OF_THE_VILLAGE ) ) {
					return false;
				}

				CompoundTag tag = data.player.getPersistentData();
				RaidData raidData = new RaidData();
				raidData.read( tag );
				if( raidData.raidId == raid.getId() ) {
					return false;
				}

				raidData.raidId = raid.getId();
				raidData.write( tag );
				return true;
			} ) ).insertTo( group );
	}

	private void addPlayerToParticipantList( OnDamaged.Data damagedData ) {
		Player player = ( Player )damagedData.attacker;
		ListTag listNBT = getOrCreateList( damagedData.target );
		CompoundTag playerNBT = createPlayerTag( player );
		if( !isPlayerInList( player, listNBT ) ) {
			listNBT.add( playerNBT );
			damagedData.target.getPersistentData().put( PARTICIPANT_LIST_TAG, listNBT );
		}
	}

	private static ListTag getOrCreateList( LivingEntity entity ) {
		CompoundTag data = entity.getPersistentData();

		return data.contains( PARTICIPANT_LIST_TAG ) ? data.getList( PARTICIPANT_LIST_TAG, 10 ) : new ListTag();
	}

	private static CompoundTag createPlayerTag( Player player ) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString( PLAYER_TAG, Utility.getPlayerUUID( player ) );

		return nbt;
	}

	private static boolean isPlayerInList( Player player, ListTag listNBT ) {
		String uuid = Utility.getPlayerUUID( player );
		for( int i = 0; i < listNBT.size(); i++ )
			if( listNBT.getCompound( i ).getString( TreasureBagManager.PLAYER_TAG ).equals( uuid ) )
				return true;

		return false;
	}

	private void rewardAllParticipants( OnDeath.Data data ) {
		LivingEntity killedEntity = data.target;
		ListTag listNBT = getOrCreateList( killedEntity );
		if( !( data.getLevel() instanceof ServerLevel level ) )
			return;

		for( int i = 0; i < listNBT.size(); i++ ) {
			giveTreasureBagTo( getPlayerByUUID( level, listNBT.getCompound( i ) ), getTreasureBag( killedEntity.getType() ), level );
		}
		listNBT.clear();
	}

	private static void giveTreasureBagTo( Player player, TreasureBagItem item, ServerLevel level ) {
		if( player == null || level == null ) // player could disconnect etc.
			return;

		ItemHelper.giveItemStackToPlayer( new ItemStack( item ), player, level );
	}

	private void giveTreasureBagToAngler( OnItemFished.Data fishedData ) {
		giveTreasureBagTo( fishedData.player, Registries.FISHING_TREASURE_BAG.get(), fishedData.getServerLevel() );
	}

	private void giveTreasureBagToHero( OnPlayerTick.Data tickData ) {
		giveTreasureBagTo( tickData.player, Registries.PILLAGER_TREASURE_BAG.get(), tickData.getServerLevel() );
	}

	static class FishingData extends SerializableStructure {
		int fishedItems = 0;

		public FishingData() {
			this.define( "TreasureBagFishingCounter", ()->this.fishedItems, x->this.fishedItems = x );
		}
	}

	static class RaidData extends SerializableStructure {
		int raidId = -1;

		public RaidData() {
			this.define( "TreasureBagLastPillagerRaidID", ()->this.raidId, x->this.raidId = x );
		}
	}

	record Register( EntityType< ? > entityType, TreasureBagItem treasureBag ) {}
}

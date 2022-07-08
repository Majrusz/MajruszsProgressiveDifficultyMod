package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.Utility;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnItemFishedContext;
import com.mlib.gamemodifiers.contexts.OnPlayerTickContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.gamemodifiers.data.OnDeathData;
import com.mlib.gamemodifiers.data.OnItemFishedData;
import com.mlib.gamemodifiers.data.OnPlayerTickData;
import com.mlib.items.ItemHelper;
import com.mlib.nbt.NBTHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Responsible for awarding the player with Treasure Bags. */
public class TreasureBagManager extends GameModifier {
	static final String PARTICIPANT_LIST_TAG = "TreasureBagPlayersToReward";
	static final String PLAYER_TAG = "TreasureBagPlayerUUID";
	static final String FISHING_TAG = "TreasureBagFishingCounter";
	static final String RAID_TAG = "TreasureBagLastPillagerRaidID";
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
		super( GameModifier.TREASURE_BAG, "", "" );

		OnDamagedContext onDamaged = new OnDamagedContext( this::addPlayerToParticipantList );
		onDamaged.addCondition( data->data.attacker instanceof Player )
			.addCondition( data->hasTreasureBag( data.target.getType() ) );

		OnDeathContext onDeath = new OnDeathContext( this::rewardAllParticipants );
		onDeath.addCondition( data->hasTreasureBag( data.target.getType() ) )
			.addCondition( data->{
				TreasureBagItem treasureBag = getTreasureBag( data.target.getType() );
				return treasureBag != null && treasureBag.isEnabled();
			} );

		OnItemFishedContext onFished = new OnItemFishedContext( this::giveTreasureBagToAngler );
		onFished.addCondition( data->data.level != null ).addCondition( data->{
			int requiredFishCount = TreasureBagItem.Fishing.REQUIRED_FISH_COUNT.getCurrentGameStageValue();
			NBTHelper.IntegerData fishedItems = new NBTHelper.IntegerData( data.player, FISHING_TAG );
			fishedItems.set( x->( x + 1 ) % requiredFishCount );

			return fishedItems.get() == 0;
		} ).addCondition( data->TreasureBagItem.Fishing.CONFIG.isEnabled() );

		OnPlayerTickContext onTick = new OnPlayerTickContext( this::giveTreasureBagToHero );
		onTick.addCondition( data->data.level != null )
			.addCondition( data->TimeHelper.hasServerTicksPassed( 20 ) )
			.addCondition( data->{
				assert data.level != null;
				Raid raid = data.level.getRaidAt( data.player.blockPosition() );
				if( raid == null || !raid.isVictory() || !data.player.hasEffect( MobEffects.HERO_OF_THE_VILLAGE ) )
					return false;

				NBTHelper.IntegerData lastRaidId = new NBTHelper.IntegerData( data.player, RAID_TAG );
				if( lastRaidId.get() == raid.getId() )
					return false;

				lastRaidId.set( raid.getId() );
				return true;
			} );

		this.addContexts( onDamaged, onDeath, onFished, onTick );
		this.addConfigs( TreasureBagItem.getConfigs() );
	}

	private void addPlayerToParticipantList( OnDamagedData damagedData ) {
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

	private void rewardAllParticipants( OnDeathData deathData ) {
		LivingEntity killedEntity = deathData.target;
		ListTag listNBT = getOrCreateList( killedEntity );
		if( deathData.level == null )
			return;

		for( int i = 0; i < listNBT.size(); i++ ) {
			giveTreasureBagTo( getPlayerByUUID( deathData.level, listNBT.getCompound( i ) ), getTreasureBag( killedEntity.getType() ), deathData.level );
		}
		listNBT.clear();
	}

	private static void giveTreasureBagTo( Player player, TreasureBagItem item, ServerLevel level ) {
		if( player == null || level == null ) // player could disconnect etc.
			return;

		ItemHelper.giveItemStackToPlayer( new ItemStack( item ), player, level );
	}

	private void giveTreasureBagToAngler( OnItemFishedData fishedData ) {
		giveTreasureBagTo( fishedData.player, Registries.FISHING_TREASURE_BAG.get(), fishedData.level );
	}

	private void giveTreasureBagToHero( OnPlayerTickData tickData ) {
		giveTreasureBagTo( tickData.player, Registries.PILLAGER_TREASURE_BAG.get(), tickData.level );
	}

	record Register( EntityType< ? > entityType, TreasureBagItem treasureBag ) {}
}

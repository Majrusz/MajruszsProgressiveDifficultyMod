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
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();
	static final OnDeathContext ON_DEATH = new OnDeathContext();
	static final OnItemFishedContext ON_FISHED = new OnItemFishedContext();
	static final OnPlayerTickContext ON_TICK = new OnPlayerTickContext();

	static {
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof Player ) );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->hasTreasureBag( data.target.getType() ) ) );

		ON_DEATH.addCondition( new Condition.ContextOnDeath( data->hasTreasureBag( data.target.getType() ) ) );
		ON_DEATH.addCondition( new Condition.ContextOnDeath( data->{
			TreasureBagItem treasureBag = getTreasureBag( data.target.getType() );
			return treasureBag != null && treasureBag.isEnabled();
		} ) );

		ON_FISHED.addCondition( new Condition.ContextOnItemFished( data->data.level != null ) );
		ON_FISHED.addCondition( new Condition.ContextOnItemFished( data->{
			int requiredFishCount = TreasureBagItem.Fishing.REQUIRED_FISH_COUNT.getCurrentGameStageValue();
			NBTHelper.IntegerData fishedItems = new NBTHelper.IntegerData( data.player, FISHING_TAG );
			fishedItems.set( x->( x + 1 ) % requiredFishCount );

			return fishedItems.get() == 0;
		} ) );
		ON_FISHED.addCondition( new Condition.ContextOnItemFished( data->TreasureBagItem.Fishing.CONFIG.isEnabled() ) );

		ON_TICK.addCondition( new Condition.ContextOnPlayerTick( data->data.level != null && TimeHelper.hasServerTicksPassed( 20 ) ) );
		ON_TICK.addCondition( new Condition.ContextOnPlayerTick( data->{
			assert data.level != null;
			Raid raid = data.level.getRaidAt( data.player.blockPosition() );
			if( raid == null || !raid.isVictory() || !data.player.hasEffect( MobEffects.HERO_OF_THE_VILLAGE ) )
				return false;

			NBTHelper.IntegerData lastRaidId = new NBTHelper.IntegerData( data.player, RAID_TAG );
			if( lastRaidId.get() == raid.getId() )
				return false;

			lastRaidId.set( raid.getId() );
			return true;
		} ) );
	}

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
		super( GameModifier.TREASURE_BAG, "", "", ON_DAMAGED, ON_DEATH, ON_FISHED, ON_TICK );
		this.addConfigs( TreasureBagItem.getConfigs() );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			addPlayerToParticipantList( damagedData );
		} else if( data instanceof OnDeathContext.Data deathData ) {
			rewardAllParticipants( deathData );
		} else if( data instanceof OnItemFishedContext.Data fishedData ) {
			giveTreasureBagToFisherman( fishedData );
		} else if( data instanceof OnPlayerTickContext.Data tickData ) {
			giveTreasureBagToHero( tickData );
		}
	}

	private void addPlayerToParticipantList( OnDamagedContext.Data damagedData ) {
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

	private static void rewardAllParticipants( OnDeathContext.Data deathData ) {
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

	private static void giveTreasureBagToFisherman( OnItemFishedContext.Data fishedData ) {
		giveTreasureBagTo( fishedData.player, Registries.FISHING_TREASURE_BAG.get(), fishedData.level );
	}

	private static void giveTreasureBagToHero( OnPlayerTickContext.Data tickData ) {
		giveTreasureBagTo( tickData.player, Registries.PILLAGER_TREASURE_BAG.get(), tickData.level );
	}

	record Register(EntityType< ? > entityType, TreasureBagItem treasureBag) {}
}

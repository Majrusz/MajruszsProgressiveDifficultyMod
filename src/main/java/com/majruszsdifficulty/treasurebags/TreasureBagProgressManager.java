package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnTreasureBagOpened;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.majruszsdifficulty.treasurebags.data.LootData;
import com.majruszsdifficulty.treasurebags.data.LootProgressData;
import com.majruszsdifficulty.treasurebags.data.TreasureBagData;
import com.mlib.ObfuscationGetter;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPlayerLogged;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class TreasureBagProgressManager extends SerializableStructure {
	static final ObfuscationGetter.Field< LootTable, List< LootPool > > POOLS = new ObfuscationGetter.Field<>( LootTable.class, "f_79109_" );
	static final ObfuscationGetter.Field< LootPool, LootPoolEntryContainer[] > ENTRIES = new ObfuscationGetter.Field<>( LootPool.class, "f_79023_" );
	final Map< String, LootProgressData > lootProgressMap = new HashMap<>();

	public TreasureBagProgressManager() {
		this.define( "LootProgress", ()->this.lootProgressMap, this.lootProgressMap::putAll, LootProgressData::new );
	}

	public void clearProgress( Player player ) {
		this.lootProgressMap.remove( player.getUUID().toString() );
		this.createDefaultProgress( player );
		this.sendMessageTo( player );
	}

	public void unlockAll( Player player ) {
		this.lootProgressMap.get( player.getUUID().toString() ).treasureBags.forEach( ( treasureBagId, treasureBagData )->{
			treasureBagData.lootDataList.forEach( lootData -> lootData.isUnlocked = true );
		} );
		this.sendMessageTo( player );
	}

	private void onLogged( OnPlayerLogged.Data data ) {
		this.createDefaultProgress( data.player );
		this.sendMessageTo( data.player );
	}

	private void updateProgress( OnTreasureBagOpened.Data data ) {
		boolean unlockedNewItems = false;
		TreasureBagData treasureBagData = this.get( data.player ).get( data.treasureBag );
		for( ItemStack itemStack : data.generatedLoot ) {
			String itemId = this.getItemId( itemStack );
			for( LootData lootData : treasureBagData.lootDataList ) {
				if( lootData.itemId.equals( itemId ) ) {
					unlockedNewItems |= !lootData.isUnlocked;

					lootData.unlock();
				}
			}
		}

		if( unlockedNewItems ) {
			this.sendMessageTo( data.player );
		}
	}

	private void createDefaultProgress( Player player ) {
		LootContext context = TreasureBagItem.generateLootContext( player );
		for( TreasureBagItem item : TreasureBagItem.TREASURE_BAGS ) {
			TreasureBagData data = this.get( player ).get( item );

			for( LootPool lootPool : POOLS.getOr( item.getLootTable(), new ArrayList<>() ) ) {
				for( LootPoolEntryContainer container : ENTRIES.getOr( lootPool, new LootPoolEntryContainer[]{} ) ) {
					LootItem lootItem = Utility.castIfPossible( LootItem.class, container );
					if( lootItem == null )
						continue;

					lootItem.createItemStack( itemStack->{
						String itemId = this.getItemId( itemStack );
						if( data.lootDataList.stream().noneMatch( lootData->lootData.itemId.equals( itemId ) ) ) {
							data.lootDataList.add( new LootData( itemId, false, lootItem.quality ) );
						}
					}, context );
				}
			}
			data.lootDataList.sort( Comparator.comparingInt( a->-a.quality ) );
		}
	}

	private void sendMessageTo( Player player ) {
		if( player instanceof ServerPlayer serverPlayer ) {
			PacketHandler.CHANNEL.send( PacketDistributor.PLAYER.with( ()->serverPlayer ), this.get( serverPlayer ) );
		}
	}

	private String getItemId( ItemStack itemStack ) {
		String itemId = Utility.getRegistryString( itemStack.getItem() );
		if( itemId.equals( "minecraft:book" ) ) {
			return "minecraft:enchanted_book";
		}

		return itemId;
	}

	private LootProgressData get( Player player ) {
		return this.lootProgressMap.computeIfAbsent( player.getUUID().toString(), key->new LootProgressData() );
	}

	@AutoInstance
	public static class Handler {
		public Handler() {
			OnPlayerLogged.listen( data->Registries.getTreasureBagProgressManager().onLogged( data ) )
				.addCondition( Condition.isServer() );

			OnTreasureBagOpened.listen( data->Registries.getTreasureBagProgressManager().updateProgress( data ) )
				.addCondition( Condition.isServer() );
		}
	}
}

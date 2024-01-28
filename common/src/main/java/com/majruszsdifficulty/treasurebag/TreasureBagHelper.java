package com.majruszsdifficulty.treasurebag;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnGameInitialized;
import com.majruszlibrary.events.OnLevelsLoaded;
import com.majruszlibrary.events.OnPlayerLoggedIn;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.platform.Services;
import com.majruszlibrary.registry.Registries;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.loot.ILootPlatform;
import com.majruszsdifficulty.mixin.IMixinLootPool;
import com.majruszsdifficulty.mixin.IMixinLootPoolSingletonContainer;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.*;

public class TreasureBagHelper {
	private static final ILootPlatform PLATFORM = Services.load( ILootPlatform.class );
	private static final List< TreasureBag > TREASURE_BAGS = new ArrayList<>();
	private static Map< String, PlayerProgress > PLAYERS = new HashMap<>();

	static {
		OnLevelsLoaded.listen( TreasureBagHelper::setupDefaultValues );

		OnGameInitialized.listen( TreasureBagHelper::updateTreasureBags );

		OnPlayerLoggedIn.listen( TreasureBagHelper::createDefaultProgress );

		OnTreasureBagOpened.listen( TreasureBagHelper::updateProgress );

		Serializables.getStatic( WorldData.class )
			.define( "treasure_bags", Reader.map( Reader.custom( PlayerProgress::new ) ), ()->PLAYERS, v->PLAYERS = v );

		Serializables.get( PlayerProgress.class )
			.define( "treasure_bags", Reader.map( Reader.custom( BagProgress::new ) ), s->s.treasureBags, ( s, v )->s.treasureBags = v );

		Serializables.get( BagProgress.class )
			.define( "items", Reader.list( Reader.custom( ItemProgress::new ) ), s->s.items, ( s, v )->s.items = v );

		Serializables.get( ItemProgress.class )
			.define( "id", Reader.location(), s->s.id, ( s, v )->s.id = v )
			.define( "is_unlocked", Reader.bool(), s->s.isUnlocked, ( s, v )->s.isUnlocked = v )
			.define( "quality", Reader.integer(), s->s.quality, ( s, v )->s.quality = v );

		Serializables.get( Progress.class )
			.define( "id", Reader.location(), s->s.id, ( s, v )->s.id = v )
			.define( "bag_progress", Reader.custom( BagProgress::new ), s->s.bagProgress, ( s, v )->s.bagProgress = v )
			.define( "unlocked_indices", Reader.list( Reader.integer() ), s->s.unlockedIndices, ( s, v )->s.unlockedIndices = v );
	}

	public static void unlockAll( Player player ) {
		PLAYERS.get( player.getStringUUID() ).treasureBags.forEach( ( id, bagProgress )->{
			List< Integer > unlockedIndices = new ArrayList<>();
			for( int idx = 0; idx < bagProgress.items.size(); ++idx ) {
				if( bagProgress.items.get( idx ).unlock() ) {
					unlockedIndices.add( idx );
				}
			}
			TreasureBagHelper.sendToPlayer( player, new ResourceLocation( id ), bagProgress, unlockedIndices );
		} );
	}

	public static void clearProgress( Player player ) {
		PLAYERS.remove( player.getStringUUID() );
		TreasureBagHelper.createDefaultProgress( player );
	}

	public static void createDefaultProgress( Player player ) {
		String uuid = player.getStringUUID();
		PlayerProgress playerProgress = PLAYERS.computeIfAbsent( uuid, key->new PlayerProgress() );
		LootContext params = LootHelper.toGiftParams( player );
		for( TreasureBag treasureBag : TREASURE_BAGS ) {
			ResourceLocation bagId = Registries.ITEMS.getId( treasureBag );
			BagProgress bagProgress = playerProgress.get( treasureBag );
			if( bagProgress.items.isEmpty() ) {
				TreasureBagHelper.createDefaultProgress( bagProgress, params, treasureBag.getLootId() );
			}

			TreasureBagHelper.sendToPlayer( player, bagId, bagProgress );
		}
		MajruszsDifficulty.WORLD_DATA.setDirty();
	}

	public static PlayerProgress getProgress( Player player ) {
		return PLAYERS.get( player.getUUID().toString() );
	}

	public static BagProgress getProgress( Player player, TreasureBag item ) {
		return TreasureBagHelper.getProgress( player ).get( item );
	}

	private static void setupDefaultValues( OnLevelsLoaded data ) {
		PLAYERS = new HashMap<>();
	}

	private static void updateTreasureBags( OnGameInitialized data ) {
		for( Item item : Registries.ITEMS ) {
			if( item instanceof TreasureBag treasureBag ) {
				TREASURE_BAGS.add( treasureBag );
			}
		}
	}

	private static void createDefaultProgress( OnPlayerLoggedIn data ) {
		TreasureBagHelper.createDefaultProgress( data.player );
	}

	private static void updateProgress( OnTreasureBagOpened data ) {
		List< Integer > unlockedIndices = new ArrayList<>();
		BagProgress bagProgress = TreasureBagHelper.getProgress( data.player, data.treasureBag );
		for( ItemStack itemStack : data.loot ) {
			for( int idx = 0; idx < bagProgress.items.size(); ++idx ) {
				ItemProgress itemProgress = bagProgress.items.get( idx );
				if( itemProgress.id.equals( Registries.ITEMS.getId( itemStack.getItem() ) ) ) {
					if( itemProgress.unlock() ) {
						unlockedIndices.add( idx );
					}
				}
			}
		}

		if( !unlockedIndices.isEmpty() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
			TreasureBagHelper.sendToPlayer( data.player, Registries.ITEMS.getId( data.treasureBag ), bagProgress, unlockedIndices );
		}
	}

	private static void sendToPlayer( Player player, ResourceLocation id, BagProgress bagProgress ) {
		TreasureBagHelper.sendToPlayer( player, id, bagProgress, List.of() );
	}

	private static void sendToPlayer( Player player, ResourceLocation id, BagProgress bagProgress, List< Integer > unlockedIndices ) {
		MajruszsDifficulty.TREASURE_BAG_PROGRESS_NETWORK.sendToClient( ( ServerPlayer )player, new Progress( id, bagProgress, unlockedIndices ) );
	}

	private static void createDefaultProgress( BagProgress bagProgress, LootContext params, ResourceLocation lootId ) {
		for( LootItem lootItem : TreasureBagHelper.getLootItems( LootHelper.getLootTable( lootId ) ) ) {
			lootItem.createItemStack( itemStack->{
				ResourceLocation itemId = TreasureBagHelper.getId( itemStack );
				if( bagProgress.items.stream().noneMatch( itemProgress->itemProgress.id.equals( itemId ) ) ) {
					bagProgress.items.add( new ItemProgress( itemId, false, ( ( IMixinLootPoolSingletonContainer )lootItem ).getQuality() ) );
				}
			}, new LootContext.Builder( params.getLevel() ).create( new LootContextParamSet.Builder().build() ) );
		}
		bagProgress.items.sort( Comparator.comparingInt( a->-a.quality ) );
	}

	private static List< LootItem > getLootItems( LootTable lootTable ) {
		return PLATFORM.getLootPools( lootTable )
			.flatMap( lootPool->Arrays.stream( ( ( IMixinLootPool )lootPool ).getEntries() ) )
			.filter( entry->entry instanceof LootItem )
			.map( entry->( LootItem )entry )
			.toList();
	}

	private static ResourceLocation getId( ItemStack itemStack ) {
		return itemStack.is( Items.BOOK ) ? new ResourceLocation( "minecraft:enchanted_book" ) : Registries.ITEMS.getId( itemStack.getItem() );
	}

	public static class PlayerProgress {
		public Map< String, BagProgress > treasureBags = new HashMap<>();

		public BagProgress get( TreasureBag item ) {
			return this.treasureBags.computeIfAbsent( Registries.ITEMS.getId( item ).toString(), key->new BagProgress() );
		}
	}

	public static class BagProgress {
		public List< ItemProgress > items = new ArrayList<>();
	}

	public static class ItemProgress {
		public ResourceLocation id;
		public boolean isUnlocked;
		public int quality;

		public ItemProgress( ResourceLocation id, boolean isUnlocked, int quality ) {
			this.id = id;
			this.isUnlocked = isUnlocked;
			this.quality = quality;
		}

		public ItemProgress() {}

		public boolean unlock() {
			boolean wasUnlockedAlready = this.isUnlocked;
			this.isUnlocked = true;

			return !wasUnlockedAlready;
		}
	}

	public static class Progress {
		public ResourceLocation id;
		public BagProgress bagProgress;
		public List< Integer > unlockedIndices;

		public Progress( ResourceLocation id, BagProgress bagProgress, List< Integer > unlockedIndices ) {
			this.id = id;
			this.bagProgress = bagProgress;
			this.unlockedIndices = unlockedIndices;
		}

		public Progress() {}
	}
}

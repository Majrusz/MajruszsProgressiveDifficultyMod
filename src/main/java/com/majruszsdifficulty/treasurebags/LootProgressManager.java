package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.PacketHandler;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.ObfuscationGetter;
import com.mlib.Utility;
import com.mlib.gamemodifiers.contexts.OnPlayerLoggedContext;
import com.mlib.gamemodifiers.data.OnPlayerLoggedData;
import com.mlib.network.message.EntityMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LootProgressManager extends GameModifier {
	static final ObfuscationGetter.Field< LootTable, List< LootPool > > POOLS = new ObfuscationGetter.Field<>( LootTable.class, "f_79109_" );
	static final ObfuscationGetter.Field< LootPool, LootPoolEntryContainer[] > ENTRIES = new ObfuscationGetter.Field<>( LootPool.class, "f_79023_" );

	public static void updateProgress( TreasureBagItem treasureBagItem, Player player, List< ItemStack > generatedLoot ) {
		String bagID = Utility.getRegistryString( treasureBagItem );
		if( bagID == null )
			return;

		for( ItemStack itemStack : generatedLoot ) {
			String itemID = Utility.getRegistryString( itemStack.getItem() );
			if( itemID == null )
				continue;

			CompoundTag compoundTag = player.getPersistentData();
			if( !compoundTag.contains( bagID ) )
				continue;

			CompoundTag treasureBagTag = compoundTag.getCompound( bagID );
			if( treasureBagTag.contains( itemID ) ) {
				LootData lootData = LootData.read( treasureBagTag, itemID );
				lootData.unlock();
				lootData.write( treasureBagTag );

				compoundTag.put( bagID, treasureBagTag );
			}
		}

		notifyPlayerAboutChanges( player, treasureBagItem );
	}

	public static void cleanProgress( Player player ) {
		for( TreasureBagItem treasureBagItem : TreasureBagItem.TREASURE_BAGS ) {
			String bagID = Utility.getRegistryString( treasureBagItem );
			if( bagID == null )
				continue;

			CompoundTag compoundTag = player.getPersistentData();
			if( compoundTag.contains( bagID ) )
				compoundTag.remove( bagID );

			createDefaultProgress( player, treasureBagItem );
		}

		notifyPlayerAboutChanges( player );
	}

	public LootProgressManager() {
		super( Registries.Modifiers.TREASURE_BAG, "LootProgressManager", "" );

		this.addContext( new OnPlayerLoggedContext( this::onLogged ) );
	}

	private void onLogged( OnPlayerLoggedData data ) {
		TreasureBagItem.TREASURE_BAGS.forEach( item->createDefaultProgress( data.player, item ) );
		notifyPlayerAboutChanges( data.player );
	}

	private static void createDefaultProgress( Player player, TreasureBagItem treasureBagItem ) {
		String bagID = Utility.getRegistryString( treasureBagItem );
		List< LootPool > pools = POOLS.get( treasureBagItem.getLootTable() );
		if( bagID == null || pools == null )
			return;

		LootContext context = TreasureBagItem.generateLootContext( player );
		for( LootPool lootPool : pools ) {
			LootPoolEntryContainer[] entries = ENTRIES.get( lootPool );
			if( entries == null )
				return;

			for( LootPoolEntryContainer entryContainer : entries ) {
				LootItem lootItem = Utility.castIfPossible( LootItem.class, entryContainer );
				if( lootItem != null )
					lootItem.createItemStack( itemStack->{
						String itemID = Utility.getRegistryString( itemStack.getItem() );
						if( itemID == null )
							return;
						if( itemID.equals( "minecraft:book" ) )
							itemID = "minecraft:enchanted_book";

						CompoundTag compoundTag = player.getPersistentData();
						CompoundTag treasureBagTag = compoundTag.contains( bagID ) ? compoundTag.getCompound( bagID ) : new CompoundTag();
						if( !treasureBagTag.contains( itemID ) ) {
							LootData lootData = new LootData( itemID, false, lootItem.quality );
							lootData.write( treasureBagTag );
						}

						compoundTag.put( bagID, treasureBagTag );
					}, context );
			}
		}
	}

	private static void notifyPlayerAboutChanges( Player player, TreasureBagItem treasureBagItem ) {
		ServerPlayer serverPlayer = Utility.castIfPossible( ServerPlayer.class, player );
		if( serverPlayer == null )
			return;

		String bagID = Utility.getRegistryString( treasureBagItem );
		if( bagID == null )
			return;

		List< LootData > lootDataList = new ArrayList<>();
		CompoundTag compoundTag = player.getPersistentData();
		if( !compoundTag.contains( bagID ) )
			return;

		CompoundTag treasureBagTag = compoundTag.getCompound( bagID );
		for( String itemID : treasureBagTag.getAllKeys() )
			lootDataList.add( LootData.read( treasureBagTag, itemID ) );

		lootDataList.sort( Comparator.comparingInt( a->-a.quality ) );
		PacketHandler.CHANNEL.send( PacketDistributor.PLAYER.with( ()->serverPlayer ), new LootProgressManager.ProgressMessage( serverPlayer, bagID, lootDataList ) );
	}

	private static void notifyPlayerAboutChanges( Player player ) {
		for( TreasureBagItem treasureBagItem : TreasureBagItem.TREASURE_BAGS )
			notifyPlayerAboutChanges( player, treasureBagItem );
	}

	public static class ProgressMessage extends EntityMessage {
		private final String treasureBagID;
		private final List< LootData > lootDataList;

		public ProgressMessage( Entity entity, String treasureBagID, List< LootData > lootDataList ) {
			super( entity );
			this.treasureBagID = treasureBagID;
			this.lootDataList = lootDataList;
		}

		public ProgressMessage( FriendlyByteBuf buffer ) {
			super( buffer );
			this.treasureBagID = buffer.readUtf();
			this.lootDataList = buffer.readList( byteBuffer->new LootData( byteBuffer.readUtf(), byteBuffer.readBoolean(), byteBuffer.readInt() ) );
		}

		@Override
		public void encode( FriendlyByteBuf buffer ) {
			super.encode( buffer );
			buffer.writeUtf( this.treasureBagID );
			buffer.writeCollection( this.lootDataList, ( byteBuffer, lootData )->{
				byteBuffer.writeUtf( lootData.itemID );
				byteBuffer.writeBoolean( lootData.isUnlocked );
				byteBuffer.writeInt( lootData.quality );
			} );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void receiveMessage( NetworkEvent.Context context ) {
			Level level = Minecraft.getInstance().level;
			if( level != null )
				LootProgressClient.generateComponents( this.treasureBagID, this.lootDataList );
		}
	}
}

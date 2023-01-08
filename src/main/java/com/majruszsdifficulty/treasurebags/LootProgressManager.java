package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.ObfuscationGetter;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnPlayerLogged;
import com.mlib.network.NetworkMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AutoInstance
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

		notifyPlayerAboutChanges( player, treasureBagItem, false );
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

		notifyPlayerAboutChanges( player, false );
	}

	public LootProgressManager() {
		super( Registries.Modifiers.TREASURE_BAG );

		new OnPlayerLogged.Context( this::onLogged )
			.insertTo( this );

		this.name( "LootProgressManager" );
	}

	private void onLogged( OnPlayerLogged.Data data ) {
		TreasureBagItem.TREASURE_BAGS.forEach( item->createDefaultProgress( data.player, item ) );
		notifyPlayerAboutChanges( data.player, true );
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

	private static void notifyPlayerAboutChanges( Player player, TreasureBagItem treasureBagItem, boolean onLogged ) {
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
		PacketHandler.CHANNEL.send( PacketDistributor.PLAYER.with( ()->serverPlayer ), new LootProgressManager.ProgressMessage( serverPlayer, bagID, lootDataList, onLogged ) );
	}

	private static void notifyPlayerAboutChanges( Player player, boolean onLogged ) {
		for( TreasureBagItem treasureBagItem : TreasureBagItem.TREASURE_BAGS )
			notifyPlayerAboutChanges( player, treasureBagItem, onLogged );
	}

	public static class ProgressMessage extends NetworkMessage {
		final int entityId;
		final String treasureBagID;
		final List< LootData > lootDataList;
		final boolean onLogged;

		public ProgressMessage( Entity entity, String treasureBagID, List< LootData > lootDataList, boolean onLogged ) {
			this.entityId = this.write( entity );
			this.treasureBagID = this.write( treasureBagID );
			this.lootDataList = this.write( lootDataList );
			this.onLogged = this.write( onLogged );
		}

		public ProgressMessage( FriendlyByteBuf buffer ) {
			this.entityId = this.readEntity( buffer );
			this.treasureBagID = this.readString( buffer );
			this.lootDataList = this.readList( buffer, LootData::new );
			this.onLogged = this.readBoolean( buffer );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void receiveMessage( NetworkEvent.Context context ) {
			Minecraft minecraft = Minecraft.getInstance();
			if( minecraft.level != null && minecraft.player != null ) {
				LootProgressClient.generateComponents( this.treasureBagID, this.lootDataList );
				if( !this.onLogged && LootProgressClient.hasUnlockedNewItems( this.treasureBagID ) ) {
					minecraft.player.sendSystemMessage( this.generateTreasureBagText() );
				}
			}
		}

		@OnlyIn( Dist.CLIENT )
		private MutableComponent generateTreasureBagText() {
			Item item = ForgeRegistries.ITEMS.getValue( new ResourceLocation( this.treasureBagID ) );
			if( item instanceof TreasureBagItem treasureBagItem ) {
				List< Component > list = new ArrayList<>();
				LootProgressClient.addDropList( treasureBagItem, list, ()->true );
				MutableComponent fullList = Component.literal( "" );
				for( int i = 0; i < list.size(); ++i ) {
					fullList.append( list.get( i ) );
					if( i < list.size() - 1 ) {
						fullList.append( "\n" );
					}
				}
				MutableComponent treasureBag = treasureBagItem.getDescription()
					.copy()
					.withStyle( style->style.withHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, fullList ) ) );
				return Component.translatable( "majruszsdifficulty.treasure_bag.new_items", ComponentUtils.wrapInSquareBrackets( treasureBag )
					.withStyle( treasureBagItem.getRarity( new ItemStack( treasureBagItem ) ).getStyleModifier() ) );
			}

			return Component.literal( "ERROR" ).withStyle( ChatFormatting.RED );
		}
	}
}

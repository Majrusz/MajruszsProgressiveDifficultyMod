package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.Utility;
import com.mlib.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn( Dist.CLIENT )
public class LootProgressClient {
	private static final Map< String, List< Component > > TREASURE_BAG_COMPONENTS = new HashMap<>();
	private static final Map< String, Tuple< Integer, Integer > > UNLOCKED_LOOT = new HashMap<>();
	private static final Map< String, Boolean > UNLOCKED_NEW_LOOT = new HashMap<>();
	private static final String HINT_TOOLTIP_TRANSLATION_KEY = "majruszsdifficulty.treasure_bag.hint_tooltip";
	private static final String LIST_TOOLTIP_TRANSLATION_KEY = "majruszsdifficulty.treasure_bag.list_tooltip";

	public static void generateComponents( String treasureBagID, List< LootData > lootDataList ) {
		int previouslyUnlockedItems = UNLOCKED_LOOT.getOrDefault( treasureBagID, new Tuple<>( 0, 1 ) ).getA();

		TREASURE_BAG_COMPONENTS.remove( treasureBagID );
		UNLOCKED_LOOT.remove( treasureBagID );
		UNLOCKED_NEW_LOOT.remove( treasureBagID );

		int unlockedItems = 0, totalItems = 0;
		List< Component > tooltip = new ArrayList<>();
		for( LootData lootData : lootDataList ) {
			MutableComponent component = new TextComponent( " " );
			if( lootData.isUnlocked ) {
				MutableComponent mutableComponent = ForgeRegistries.ITEMS.getValue( new ResourceLocation( lootData.itemID ) ).getDescription().copy();
				component.append( mutableComponent.withStyle( getUnlockedItemFormat( lootData.quality ) ) );
			} else {
				component.append( new TextComponent( "???" ).withStyle( getLockedItemFormat( lootData.quality ) ) );
			}

			if( lootData.isUnlocked )
				++unlockedItems;
			++totalItems;

			tooltip.add( component );
		}

		UNLOCKED_NEW_LOOT.put( treasureBagID, unlockedItems > previouslyUnlockedItems );
		UNLOCKED_LOOT.put( treasureBagID, new Tuple<>( unlockedItems, totalItems ) );
		TREASURE_BAG_COMPONENTS.put( treasureBagID, tooltip );
	}

	public static void addDropList( TreasureBagItem treasureBagItem, List< Component > tooltip, Supplier< Boolean > isDetailed ) {
		if( isDetailed.get() ) {
			String bagID = Utility.getRegistryString( treasureBagItem );
			if( bagID == null )
				return;

			Tuple< Integer, Integer > tuple = UNLOCKED_LOOT.get( bagID );
			tooltip.add( new TranslatableComponent( LIST_TOOLTIP_TRANSLATION_KEY, tuple.getA(), tuple.getB() ).withStyle( ChatFormatting.GRAY ) );
			if( LootProgressClient.TREASURE_BAG_COMPONENTS.containsKey( bagID ) )
				tooltip.addAll( LootProgressClient.TREASURE_BAG_COMPONENTS.get( bagID ) );
		} else {
			tooltip.add( new TranslatableComponent( HINT_TOOLTIP_TRANSLATION_KEY ).withStyle( ChatFormatting.GRAY ) );
		}
	}

	public static boolean hasUnlockedNewItems( String treasureBagID ) {
		return UNLOCKED_NEW_LOOT.getOrDefault( treasureBagID, false );
	}

	protected static ChatFormatting getUnlockedItemFormat( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.LIGHT_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.GREEN;
			default -> ChatFormatting.GRAY;
		};
	}

	protected static ChatFormatting getLockedItemFormat( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.DARK_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.DARK_GREEN;
			default -> ChatFormatting.DARK_GRAY;
		};
	}
}
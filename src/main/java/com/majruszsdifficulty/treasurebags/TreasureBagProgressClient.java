package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.items.TreasureBagItem;
import com.majruszsdifficulty.treasurebags.data.LootData;
import com.majruszsdifficulty.treasurebags.data.LootProgressData;
import com.mlib.Utility;
import com.mlib.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class TreasureBagProgressClient {
	static final Map< String, List< Component > > TREASURE_BAG_COMPONENTS = new HashMap<>();
	static LootProgressData LOOT_PROGRESS_DATA = new LootProgressData();

	public static void handle( Player player, LootProgressData data ) {
		generateTextComponents( data );
		if( LOOT_PROGRESS_DATA.treasureBags.size() == data.treasureBags.size() ) {
			tryToSendNewLootMessage( player, data );
		}

		LOOT_PROGRESS_DATA = data;
	}

	public static List< Component > getTextComponents( TreasureBagItem item ) {
		if( ClientHelper.isShiftDown() ) {
			return TREASURE_BAG_COMPONENTS.get( Utility.getRegistryString( item ) );
		} else {
			return List.of( Component.translatable( "majruszsdifficulty.treasure_bag.hint_tooltip" ).withStyle( ChatFormatting.GRAY ) );
		}
	}

	private static void generateTextComponents( LootProgressData data ) {
		for( String treasureBagId : data.treasureBags.keySet() ) {
			List< LootData > lootDataList = data.treasureBags.get( treasureBagId ).lootDataList;
			List< Component > tooltip = new ArrayList<>();
			lootDataList.forEach( lootData->tooltip.add( Component.literal( " " ).append( getTextComponent( lootData ) ) ) );
			tooltip.add( 0, getProgressComponent( lootDataList ) );

			TREASURE_BAG_COMPONENTS.put( treasureBagId, tooltip );
		}
	}

	private static MutableComponent getTextComponent( LootData lootData ) {
		if( lootData.isUnlocked ) {
			return ForgeRegistries.ITEMS.getValue( new ResourceLocation( lootData.itemId ) )
				.getDescription()
				.copy()
				.withStyle( getUnlockedItemFormat( lootData.quality ) );
		} else {
			return Component.literal( "???" ).withStyle( getLockedItemFormat( lootData.quality ) );
		}
	}

	private static MutableComponent getProgressComponent( List< LootData > lootDataList ) {
		int unlockedItems = ( int )lootDataList.stream().filter( data->data.isUnlocked ).count();
		int totalItems = lootDataList.size();

		return Component.translatable( "majruszsdifficulty.treasure_bag.list_tooltip", unlockedItems, totalItems )
			.withStyle( ChatFormatting.GRAY );
	}

	private static ChatFormatting getUnlockedItemFormat( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.LIGHT_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.GREEN;
			default -> ChatFormatting.GRAY;
		};
	}

	private static ChatFormatting getLockedItemFormat( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.DARK_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.DARK_GREEN;
			default -> ChatFormatting.DARK_GRAY;
		};
	}

	private static void tryToSendNewLootMessage( Player player, LootProgressData data ) {
		for( String treasureBagId : data.treasureBags.keySet() ) {
			List< LootData > lootDataList = data.treasureBags.get( treasureBagId ).lootDataList;
			List< LootData > lootDataOldList = LOOT_PROGRESS_DATA.treasureBags.get( treasureBagId ).lootDataList;
			if( lootDataList.size() != lootDataOldList.size() )
				continue;

			for( int i = 0; i < lootDataList.size(); ++i ) {
				if( lootDataList.get( i ).isUnlocked && !lootDataOldList.get( i ).isUnlocked ) {
					player.sendSystemMessage( getMessageComponent( treasureBagId, lootDataList, lootDataOldList ) );
					break;
				}
			}
		}
	}

	private static MutableComponent getMessageComponent( String treasureBagId, List< LootData > lootDataList, List< LootData > lootDataOldList ) {
		Item item = ForgeRegistries.ITEMS.getValue( new ResourceLocation( treasureBagId ) );
		if( item instanceof TreasureBagItem treasureBagItem ) {
			MutableComponent treasureBag = treasureBagItem.getDescription()
				.copy()
				.withStyle( style->style.withHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, getHoverComponent( treasureBagId, lootDataList, lootDataOldList ) ) ) );

			return Component.translatable( "majruszsdifficulty.treasure_bag.new_items", ComponentUtils.wrapInSquareBrackets( treasureBag )
				.withStyle( treasureBagItem.getRarity( new ItemStack( treasureBagItem ) ).getStyleModifier() ) );
		}

		return Component.literal( "ERROR" ).withStyle( ChatFormatting.RED );
	}

	private static MutableComponent getHoverComponent( String treasureBagId, List< LootData > lootDataList, List< LootData > lootDataOldList ) {
		MutableComponent tooltip = TREASURE_BAG_COMPONENTS.get( treasureBagId ).get( 0 ).copy();
		for( int i = 0; i < lootDataList.size(); ++i ) {
			tooltip.append( "\n" );
			MutableComponent component = TREASURE_BAG_COMPONENTS.get( treasureBagId ).get( i + 1 ).copy();
			if( lootDataList.get( i ).isUnlocked && !lootDataOldList.get( i ).isUnlocked ) {
				component.withStyle( ChatFormatting.BOLD );
			}
			tooltip.append( component );
		}

		return tooltip;
	}
}
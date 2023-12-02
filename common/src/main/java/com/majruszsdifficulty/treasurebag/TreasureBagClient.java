package com.majruszsdifficulty.treasurebag;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.client.ClientHelper;
import com.majruszlibrary.events.OnItemTooltip;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.registry.Registries;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.TreasureBag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class TreasureBagClient {
	private static final Map< String, List< Component > > COMPONENTS = new HashMap<>();

	static {
		OnItemTooltip.listen( TreasureBagClient::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof TreasureBag );

		MajruszsDifficulty.Network.TREASURE_BAG_PROGRESS.addClientCallback( TreasureBagClient::onProgressReceived );
	}

	private static void addTooltip( OnItemTooltip data ) {
		if( data.isAdvanced() ) {
			data.components.add( TextHelper.translatable( "majruszsdifficulty.treasure_bag.item_tooltip" ).withStyle( ChatFormatting.GRAY ) );
			data.components.add( TextHelper.empty() );
		}

		if( ClientHelper.isShiftDown() ) {
			List< Component > components = COMPONENTS.get( Registries.ITEMS.getId( data.itemStack.getItem() ).toString() );
			if( components == null ) {
				return;
			}

			data.components.addAll( components );
		} else {
			data.components.add( TextHelper.translatable( "majruszsdifficulty.treasure_bag.hint_tooltip" ).withStyle( ChatFormatting.GRAY ) );
		}
	}

	private static void onProgressReceived( TreasureBagHelper.Progress data ) {
		List< Component > components = COMPONENTS.computeIfAbsent( data.id.toString(), id->new ArrayList<>() );
		components.clear();
		components.add( TreasureBagClient.toProgressComponent( data.bagProgress ) );
		for( TreasureBagHelper.ItemProgress itemProgress : data.bagProgress.items ) {
			components.add( TextHelper.literal( " " ).append( TreasureBagClient.toComponent( itemProgress ) ) );
		}
		if( data.unlockedIndices.size() > 0 ) {
			TreasureBagClient.sendToChat( data );
		}
	}

	private static void sendToChat( TreasureBagHelper.Progress data ) {
		List< Component > components = new ArrayList<>();
		for( int idx = 0; idx < data.bagProgress.items.size(); ++idx ) {
			components.add( TextHelper.literal( data.unlockedIndices.contains( idx ) ? "+" : " " )
				.withStyle( ChatFormatting.DARK_GREEN )
				.append( TreasureBagClient.toComponent( data.bagProgress.items.get( idx ) ) )
			);
		}

		Item item = Registries.ITEMS.get( data.id );
		MutableComponent message = TextHelper.translatable( "majruszsdifficulty.treasure_bag.new_items", ComponentUtils.wrapInSquareBrackets( item.getDescription() )
			.withStyle( item.getRarity( new ItemStack( item ) ).color ) );
		MutableComponent description = TreasureBagClient.toProgressComponent( data.bagProgress );
		components.forEach( component->description.append( TextHelper.literal( "\n" ).append( component ) ) );

		Side.getLocalPlayer()
			.sendSystemMessage( message.withStyle( style->style.withHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, description ) ) ) );
	}

	private static MutableComponent toProgressComponent( TreasureBagHelper.BagProgress bagProgress ) {
		long unlockedItems = bagProgress.items.stream().filter( item->item.isUnlocked ).count();
		int totalItems = bagProgress.items.size();

		return TextHelper.translatable( "majruszsdifficulty.treasure_bag.list_tooltip", unlockedItems, totalItems ).withStyle( ChatFormatting.GRAY );
	}

	private static Component toComponent( TreasureBagHelper.ItemProgress itemProgress ) {
		if( itemProgress.isUnlocked ) {
			return Registries.ITEMS.get( itemProgress.id )
				.getDescription()
				.copy()
				.withStyle( TreasureBagClient.getUnlockedFormatting( itemProgress.quality ) );
		} else {
			return TextHelper.literal( "???" )
				.withStyle( TreasureBagClient.getLockedFormatting( itemProgress.quality ) );
		}
	}

	private static ChatFormatting getUnlockedFormatting( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.LIGHT_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.GREEN;
			default -> ChatFormatting.GRAY;
		};
	}

	private static ChatFormatting getLockedFormatting( int quality ) {
		return switch( quality ) {
			case 4 -> ChatFormatting.GOLD;
			case 3 -> ChatFormatting.DARK_PURPLE;
			case 2 -> ChatFormatting.BLUE;
			case 1 -> ChatFormatting.DARK_GREEN;
			default -> ChatFormatting.DARK_GRAY;
		};
	}
}

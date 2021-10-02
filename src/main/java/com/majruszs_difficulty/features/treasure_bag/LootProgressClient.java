package com.majruszs_difficulty.features.treasure_bag;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class LootProgressClient {
	public static final Map< String, List< Component > > TREASURE_BAG_COMPONENTS = new HashMap<>();

	public static void generateComponents( String treasureBagID, List< String > items ) {
		TREASURE_BAG_COMPONENTS.remove( treasureBagID );

		List< Component > tooltip = new ArrayList<>();
		for( String itemID : items ) {
			MutableComponent component = new TextComponent( " " );
			if( itemID.equals( "???" ) ) {
				component.append( new TextComponent( "???" ).withStyle( ChatFormatting.DARK_GRAY ) );
			} else {
				component.append( Registry.ITEM.get( new ResourceLocation( itemID ) ).getDescription() );
			}
			tooltip.add( component );
		}
		TREASURE_BAG_COMPONENTS.put( treasureBagID, tooltip );
	}
}
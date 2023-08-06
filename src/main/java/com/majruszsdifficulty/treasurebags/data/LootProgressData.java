package com.majruszsdifficulty.treasurebags.data;

import com.majruszsdifficulty.items.TreasureBagItem;
import com.majruszsdifficulty.treasurebags.TreasureBagProgressClient;
import com.mlib.Utility;
import com.mlib.data.SerializableMap;
import com.mlib.data.SerializableStructure;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;

public class LootProgressData extends SerializableMap {
	public Map< String, TreasureBagData > treasureBags = new HashMap<>();

	public LootProgressData() {
		super( "TreasureBags" );

		this.defineCustom( ()->this.treasureBags, x->this.treasureBags = x, TreasureBagData::new );
	}

	public TreasureBagData get( TreasureBagItem item ) {
		return this.treasureBags.computeIfAbsent( Utility.getRegistryString( item ), key->new TreasureBagData() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void onClient( NetworkEvent.Context context ) {
		Minecraft minecraft = Minecraft.getInstance();
		if( minecraft.player != null ) {
			TreasureBagProgressClient.handle( minecraft.player, this );
		}
	}
}

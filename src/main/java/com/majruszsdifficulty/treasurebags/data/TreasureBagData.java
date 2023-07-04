package com.majruszsdifficulty.treasurebags.data;

import com.mlib.data.SerializableList;
import com.mlib.data.SerializableStructure;

import java.util.ArrayList;
import java.util.List;

public class TreasureBagData extends SerializableList {
	public List< LootData > lootDataList;

	public TreasureBagData( List< LootData > lootDataList ) {
		super( "LootDataList" );

		this.lootDataList = lootDataList;

		this.defineCustom( ()->this.lootDataList, x->this.lootDataList = x, LootData::new );
	}

	public TreasureBagData() {
		this( new ArrayList<>() );
	}
}

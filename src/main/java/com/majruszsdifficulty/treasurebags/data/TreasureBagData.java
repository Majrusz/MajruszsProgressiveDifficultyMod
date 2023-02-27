package com.majruszsdifficulty.treasurebags.data;

import com.mlib.data.SerializableStructure;

import java.util.ArrayList;
import java.util.List;

public class TreasureBagData extends SerializableStructure {
	public final List< LootData > lootDataList;

	public TreasureBagData( List< LootData > lootDataList ) {
		this.lootDataList = lootDataList;

		this.define( "LootDataList", ()->this.lootDataList, this.lootDataList::addAll, LootData::new );
	}

	public TreasureBagData() {
		this( new ArrayList<>() );
	}
}

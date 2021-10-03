package com.majruszs_difficulty.features.treasure_bag;

import net.minecraft.nbt.CompoundTag;

public class LootData {
	public static final String UNLOCKED_TAG = "unlocked";
	public static final String QUALITY_TAG = "quality";

	public String itemID;
	public boolean isUnlocked;
	public int quality;

	public LootData( String itemID, boolean isUnlocked, int quality ) {
		this.itemID = itemID;
		this.isUnlocked = isUnlocked;
		this.quality = quality;
	}

	public void write( CompoundTag treasureBagTag ) {
		CompoundTag itemTag = new CompoundTag();
		itemTag.putBoolean( LootData.UNLOCKED_TAG, this.isUnlocked );
		itemTag.putInt( LootData.QUALITY_TAG, this.quality );

		treasureBagTag.put( this.itemID, itemTag );
	}

	public static LootData read( CompoundTag treasureBagTag, String itemID ) {
		CompoundTag itemTag = treasureBagTag.getCompound( itemID );
		boolean isUnlocked = itemTag.getBoolean( LootData.UNLOCKED_TAG );
		int quality = itemTag.getInt( LootData.QUALITY_TAG );

		return new LootData( itemID, isUnlocked, quality );
	}
}

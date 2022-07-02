package com.majruszsdifficulty.treasurebags;

import net.minecraft.nbt.CompoundTag;

public class LootData {
	static final String UNLOCKED_TAG = "unlocked";
	static final String QUALITY_TAG = "quality";
	final String itemID;
	boolean isUnlocked;
	final int quality;

	public LootData( String itemID, boolean isUnlocked, int quality ) {
		this.itemID = itemID;
		this.isUnlocked = isUnlocked;
		this.quality = quality;
	}

	public void unlock() {
		this.isUnlocked = true;
	}

	public void write( CompoundTag treasureBagTag ) {
		CompoundTag itemTag = new CompoundTag();
		itemTag.putBoolean( LootData.UNLOCKED_TAG, this.isUnlocked );
		itemTag.putInt( LootData.QUALITY_TAG, this.quality );

		treasureBagTag.put( this.itemID, itemTag );
	}

	public static LootData read( CompoundTag treasureBagTag, String itemID ) {
		CompoundTag itemTag = treasureBagTag.getCompound( itemID );
		return new LootData( itemID, itemTag.getBoolean( LootData.UNLOCKED_TAG ), itemTag.getInt( LootData.QUALITY_TAG ) );
	}
}

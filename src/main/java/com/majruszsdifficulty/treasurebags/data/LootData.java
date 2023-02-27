package com.majruszsdifficulty.treasurebags.data;

import com.mlib.data.SerializableStructure;

public class LootData extends SerializableStructure {
	public String itemId;
	public boolean isUnlocked;
	public int quality;

	public LootData() {
		this.define( "item", ()->this.itemId, x->this.itemId = x );
		this.define( "unlocked", ()->this.isUnlocked, x->this.isUnlocked = x );
		this.define( "quality", ()->this.quality, x->this.quality = x );
	}

	public LootData( String itemId, boolean isUnlocked, int quality ) {
		this();

		this.itemId = itemId;
		this.isUnlocked = isUnlocked;
		this.quality = quality;
	}

	public void unlock() {
		this.isUnlocked = true;
	}
}

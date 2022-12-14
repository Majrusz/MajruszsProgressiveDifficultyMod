package com.majruszsdifficulty.treasurebags;

import com.mlib.network.NetworkMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class LootData extends NetworkMessage {
	static final String UNLOCKED_TAG = "unlocked";
	static final String QUALITY_TAG = "quality";
	final String itemID;
	boolean isUnlocked;
	final int quality;

	public LootData( String itemID, boolean isUnlocked, int quality ) {
		this.itemID = this.write( itemID );
		this.isUnlocked = this.write( isUnlocked );
		this.quality = this.write( quality );
	}

	public LootData( FriendlyByteBuf buffer ) {
		this.itemID = this.readString( buffer );
		this.isUnlocked = this.readBoolean( buffer );
		this.quality = this.readInt( buffer );
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

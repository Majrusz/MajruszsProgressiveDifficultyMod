package com.majruszs_difficulty.events;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreasureBagManager {
	private static final List< Register > registers = new ArrayList<>();

	public static void addTreasureBagTo( EntityType< ? > entityType, Item treasureBag ) {
		registers.add( new Register( entityType, treasureBag ) );
	}

	public static boolean hasTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return true;

		return false;
	}

	@Nullable
	public static Item getTreasureBag( EntityType< ? > entityType ) {
		for( Register register : registers )
			if( register.entityType.equals( entityType ) )
				return register.treasureBag;

		return null;
	}

	static class Register {
		public final EntityType< ? > entityType;
		public final Item treasureBag;

		public Register( EntityType< ? > entityType, Item treasureBag ) {
			this.entityType = entityType;
			this.treasureBag = treasureBag;
		}
	}
}

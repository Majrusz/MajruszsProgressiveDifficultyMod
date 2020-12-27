package com.majruszs_difficulty.items;

import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.item.Item;

public class UndeadBattleStandard extends Item {
	public UndeadBattleStandard() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( RegistryHandler.ITEM_GROUP ) );
	}
}

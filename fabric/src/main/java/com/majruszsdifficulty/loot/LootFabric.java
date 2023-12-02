package com.majruszsdifficulty.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Arrays;
import java.util.stream.Stream;

public class LootFabric implements ILootPlatform {
	@Override
	public Stream< LootPool > getLootPools( LootTable lootTable ) {
		return Arrays.stream( lootTable.pools );
	}
}

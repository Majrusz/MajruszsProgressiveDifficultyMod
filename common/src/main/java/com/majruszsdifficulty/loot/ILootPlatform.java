package com.majruszsdifficulty.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.stream.Stream;

public interface ILootPlatform {
	Stream< LootPool > getLootPools( LootTable lootTable );
}

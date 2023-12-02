package com.majruszsdifficulty.loot;

import com.majruszsdifficulty.mixin.forge.IMixinLootTable;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.stream.Stream;

public class LootNeoForge implements ILootPlatform {
	@Override
	public Stream< LootPool > getLootPools( LootTable lootTable ) {
		return ( ( IMixinLootTable )lootTable ).getPools().stream();
	}
}

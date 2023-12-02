package com.majruszsdifficulty.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( LootPool.class )
public interface IMixinLootPool {
	@Accessor( "entries" )
	LootPoolEntryContainer[] getEntries();
}

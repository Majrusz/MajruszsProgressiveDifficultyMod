package com.majruszsdifficulty.mixin;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( LootPoolSingletonContainer.class )
public interface IMixinLootPoolSingletonContainer {
	@Accessor( "quality" )
	int getQuality();
}

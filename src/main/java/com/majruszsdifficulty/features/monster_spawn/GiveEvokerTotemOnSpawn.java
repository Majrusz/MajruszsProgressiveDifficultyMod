package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameStage;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Gives Totem for Evoker on spawn. */
public class GiveEvokerTotemOnSpawn extends GiveItemAfterSpawningBase {
	private static final String CONFIG_NAME = "EvokerTotem";
	private static final String CONFIG_COMMENT = "Evoker spawns with Totem of Undying.";

	public GiveEvokerTotemOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameStage.Stage.NORMAL, false, EquipmentSlot.MAINHAND, false, false );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Evoker && super.shouldBeExecuted( entity );
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( Items.TOTEM_OF_UNDYING );
	}
}

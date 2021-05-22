package com.majruszs_difficulty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class MajruszsHelper {
	/**
	 Returns player from given damage source.
	 Returns null if casting was impossible.
	 */
	@Nullable
	public static PlayerEntity getPlayerFromDamageSource( DamageSource damageSource ) {
		return damageSource.getTrueSource() instanceof PlayerEntity ? ( PlayerEntity )damageSource.getTrueSource() : null;
	}

	/**
	 Checking if given entity is human.

	 @param entity Entity to test.
	 */
	public static boolean isHuman( @Nullable Entity entity ) {
		return entity instanceof PlayerEntity || entity instanceof VillagerEntity || entity instanceof PillagerEntity || entity instanceof WitchEntity;
	}

	/**
	 Checking if given entity is animal.

	 @param entity Entity to test.
	 */
	public static boolean isAnimal( @Nullable Entity entity ) {
		return entity instanceof AnimalEntity;
	}
}

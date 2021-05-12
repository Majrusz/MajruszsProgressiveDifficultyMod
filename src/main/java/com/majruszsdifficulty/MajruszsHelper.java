package com.majruszsdifficulty;

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
	@Nullable
	public static PlayerEntity getPlayerFromDamageSource( DamageSource damageSource ) {
		if( !( damageSource.getTrueSource() instanceof PlayerEntity ) )
			return null;

		return ( PlayerEntity )damageSource.getTrueSource();
	}

	@Nullable
	public static ServerWorld getServerWorldFromEntity( Entity entity ) {
		if( !( entity.world instanceof ServerWorld ) )
			return null;

		return ( ServerWorld )entity.world;
	}

	public static void giveItemStackToPlayer( ItemStack itemStack, PlayerEntity player, ServerWorld world ) {
		if( !player.inventory.addItemStackToInventory( itemStack ) ) {
			double x = player.getPosX(), y = player.getPosY() + 1.0, z = player.getPosZ();
			world.addEntity( new ItemEntity( world, x, y, z, itemStack ) );
		}
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

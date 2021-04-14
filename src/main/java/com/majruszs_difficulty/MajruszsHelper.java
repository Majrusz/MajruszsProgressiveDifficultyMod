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
	@Nullable
	public static PlayerEntity getPlayerFromDamageSource( DamageSource damageSource ) {
		if( !( damageSource.getDirectEntity() instanceof PlayerEntity ) )
			return null;

		return ( PlayerEntity )damageSource.getDirectEntity();
	}

	@Nullable
	public static ServerWorld getServerWorldFromEntity( Entity entity ) {
		if( !( entity.getCommandSenderWorld() instanceof ServerWorld ) )
			return null;

		return ( ServerWorld )entity.getCommandSenderWorld();
	}

	public static void giveItemStackToPlayer( ItemStack itemStack, PlayerEntity player, ServerWorld world ) {
		if( !player.inventory.add( itemStack ) ) {
			double x = player.getX(), y = player.getY() + 1.0, z = player.getZ();
			world.addFreshEntity( new ItemEntity( world, x, y, z, itemStack ) );
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

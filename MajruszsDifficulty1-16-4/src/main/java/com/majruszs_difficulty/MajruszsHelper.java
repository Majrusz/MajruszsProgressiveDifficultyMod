package com.majruszs_difficulty;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class MajruszsHelper {
	public static boolean isHostile( LivingEntity livingEntity ) {
		ModifiableAttributeInstance damageAttribute = livingEntity.getAttribute( Attributes.ATTACK_DAMAGE );

		if( damageAttribute != null && damageAttribute.getValue() > 0.0D )
			return true;

		return false;
	}

	public static double getClampedRegionalDifficulty( LivingEntity livingEntity, ServerWorld world ) {
		return world.getDifficultyForLocation( new BlockPos( livingEntity.getPositionVec() ) )
			.getClampedAdditionalDifficulty();
	}

	public static final int ticksInSecond = 20;

	public static int secondsToTicks( double seconds ) {
		return ( int )( seconds * ticksInSecond );
	}

	public static final int ticksInMinute = ticksInSecond * 60;

	public static int minutesToTicks( double minutes ) {
		return ( int )( minutes * ticksInMinute );
	}

	public static ItemStack damageItem( ItemStack itemStack ) {
		itemStack.setDamage( MajruszsDifficulty.RANDOM.nextInt( itemStack.getMaxDamage() / 2 ) );

		return itemStack;
	}

	public static ItemStack enchantItem( ItemStack itemStack, double clampedRegionalDifficulty ) {
		return EnchantmentHelper.addRandomEnchantment( MajruszsDifficulty.RANDOM, itemStack, ( int )( 5 + 18 * clampedRegionalDifficulty ), false );
	}

	public static ItemStack tryEnchantItem( ItemStack itemStack, double clampedRegionalDiffculty, double chance ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= chance * clampedRegionalDiffculty )
			return itemStack;

		return enchantItem( itemStack, clampedRegionalDiffculty );
	}

	public static ItemStack tryEnchantWeapon( ItemStack itemStack, double clampedRegionalDifficulty ) {
		return tryEnchantItem( itemStack, clampedRegionalDifficulty, 0.25 );
	}

	public static ItemStack tryEnchantArmor( ItemStack itemStack, double clampedRegionalDifficulty ) {
		return tryEnchantItem( itemStack, clampedRegionalDifficulty, 0.5 );
	}

	public static ItemStack damageAndEnchantItem( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( itemStack.getItem() instanceof ArmorItem )
			itemStack = tryEnchantArmor( itemStack, clampedRegionalDifficulty );
		else
			itemStack = tryEnchantWeapon( itemStack, clampedRegionalDifficulty );

		return damageItem( itemStack );
	}

	public static boolean isPlayerIn( PlayerEntity player, RegistryKey< DimensionType > dimensionType ) {
		return player.world.getDimensionType()
			.getEffects()
			.equals( dimensionType.getLocation() );
	}

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
}

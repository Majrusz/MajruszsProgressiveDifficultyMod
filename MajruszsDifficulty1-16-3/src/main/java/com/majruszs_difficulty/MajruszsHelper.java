package com.majruszs_difficulty;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class MajruszsHelper {
	public static boolean isHostile( LivingEntity livingEntity ) {
		ModifiableAttributeInstance damageAttribute = livingEntity.getAttribute( Attributes.field_233823_f_ );

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

	public static ItemStack tryEnchantWeapon( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= 0.25D * clampedRegionalDifficulty )
			return itemStack;

		return enchantItem( itemStack, clampedRegionalDifficulty );
	}

	public static ItemStack tryEnchantArmor( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= 0.5D * clampedRegionalDifficulty )
			return itemStack;

		return enchantItem( itemStack, clampedRegionalDifficulty );
	}

	public static ItemStack enchantItem( ItemStack itemStack, double clampedRegionalDifficulty ) {
		return EnchantmentHelper.addRandomEnchantment( MajruszsDifficulty.RANDOM, itemStack, ( int )( 5 + 18 * clampedRegionalDifficulty ), false );
	}

	public static ItemStack damageAndEnchantItemStack( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( itemStack.getItem() instanceof ArmorItem )
			itemStack = tryEnchantArmor( itemStack, clampedRegionalDifficulty );
		else
			itemStack = tryEnchantWeapon( itemStack, clampedRegionalDifficulty );

		return damageItem( itemStack );
	}
}

package com.majruszs_difficulty;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class MajruszsHelper {
	public static final int ticksInSecond = 20;
	public static final int ticksInMinute = ticksInSecond * 60;

	public static boolean isHostile( LivingEntity livingEntity ) {
		ModifiableAttributeInstance damageAttribute = livingEntity.getAttribute( Attributes.ATTACK_DAMAGE );

		return damageAttribute != null && damageAttribute.getValue() > 0.0D;
	}

	public static double getClampedRegionalDifficulty( LivingEntity livingEntity, ServerWorld world ) {
		return world.getDifficultyForLocation( new BlockPos( livingEntity.getPositionVec() ) )
			.getClampedAdditionalDifficulty();
	}

	public static int secondsToTicks( double seconds ) {
		return ( int )( seconds * ticksInSecond );
	}

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

	public static boolean isPlayerIn( PlayerEntity player, RegistryKey< DimensionType > dimensionTypeRegistryKey ) {
		Registry< DimensionType > registry = player.world.func_241828_r()
			.func_230520_a_();

		return dimensionTypeRegistryKey.getLocation()
			.equals( registry.getKey( player.world.getDimensionType() ) );
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

	/**
	 Returns resource location relative to mod id.

	 @param location Path to desired file.

	 @return Returns new instance of resource location.
	 */
	public static ResourceLocation getResource( String location ) {
		return new ResourceLocation( MajruszsDifficulty.MOD_ID, location );
	}

	/**
	 Returns information whether the number drawn was within the range [0;chance].

	 @param chance Chance of happening. [0.0;1.0]
	 */
	public static boolean tryChance( double chance ) {
		return MajruszsDifficulty.RANDOM.nextDouble() <= chance;
	}

	/**
	 Returns difficulty instance of position where entity is currently at.

	 @param entity Entity to get difficulty instance.
	 */
	public static DifficultyInstance getDifficultyInstance( LivingEntity entity ) {
		return entity.world.getDifficultyForLocation( new BlockPos( entity.getPositionVec() ) );
	}

	/**
	 Returns current clamped regional difficulty. (range [0.0;1.0])

	 @param entity Entity where difficulty should be calculated.
	 */
	public static double getClampedRegionalDifficulty( LivingEntity entity ) {
		DifficultyInstance difficultyInstance = getDifficultyInstance( entity );

		return difficultyInstance.getClampedAdditionalDifficulty();
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

	/**
	 Adds potion effect to entity only when effect is applicable.

	 @param entity         Entity to add potion effect.
	 @param effectInstance Instance of desired effect.
	 */
	public static void applyEffectIfPossible( LivingEntity entity, EffectInstance effectInstance ) {
		if( entity.isPotionApplicable( effectInstance ) )
			entity.addPotionEffect( effectInstance );
	}

	/**
	 Adds potion effect to entity only when effect is applicable.

	 @param entity         Entity to add potion effect.
	 @param effect         Desired effect.
	 @param effectDuration Effect duration in ticks.
	 @param amplifier      Effect amplifier, level.
	 */
	public static void applyEffectIfPossible( LivingEntity entity, Effect effect, int effectDuration, int amplifier ) {
		applyEffectIfPossible( entity, new EffectInstance( effect, effectDuration, amplifier ) );
	}
}

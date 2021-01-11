package com.majruszs_difficulty.items;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

/** Adding custom item tiers to the game. */
public enum CustomItemTier implements IItemTier {
	WITHER( 360, 3, 15, 5.0f, 3.0f, () -> Ingredient.fromItems( Items.BONE ) );

	protected final int maxUses, harvestLevel, enchantability;
	protected final float efficiency, attackDamage;
	protected final Supplier< Ingredient > repairMaterial;

	CustomItemTier( int uses, int harvestLevel, int enchantability, float efficiency, float damage, Supplier< Ingredient > material ) {
		this.maxUses = uses;
		this.harvestLevel = harvestLevel;
		this.enchantability = enchantability;
		this.efficiency = efficiency;
		this.attackDamage = damage;
		this.repairMaterial = material;
	}

	@Override
	public int getMaxUses() {
		return this.maxUses;
	}

	@Override
	public float getEfficiency() {
		return this.efficiency;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.get();
	}
}

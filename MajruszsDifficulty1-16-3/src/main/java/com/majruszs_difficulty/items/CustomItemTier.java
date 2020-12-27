package com.majruszs_difficulty.items;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum CustomItemTier implements IItemTier {
	WITHER( 250, 5.0f, 3.0f, 3, 15, () -> Ingredient.fromItems( Items.BONE ) );

	protected final int maxUses;
	protected final float efficiency;
	protected final float attackDamage;
	protected final int harvestLevel;
	protected final int enchantability;
	protected final Supplier< Ingredient > repairMaterial;

	CustomItemTier( int uses, float efficiency, float damage, int harvestLevel, int enchantability, Supplier< Ingredient > material ) {
		this.maxUses = uses;
		this.efficiency = efficiency;
		this.attackDamage = damage;
		this.harvestLevel = harvestLevel;
		this.enchantability = enchantability;
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

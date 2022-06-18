package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/** Adding custom item tiers to the game. */
public enum CustomItemTier implements Tier {
	WITHER( 360, 3, 15, 5.0f, 3.0f, ()->Ingredient.of( Items.BONE ) ), END( 2137,
		4,
		15,
		10.0f,
		5.0f,
		()->Ingredient.of( Registries.END_INGOT.get() )
	);

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
	public int getUses() {
		return this.maxUses;
	}

	@Override
	public float getSpeed() {
		return this.efficiency;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamage;
	}

	@Override
	public int getLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}
}

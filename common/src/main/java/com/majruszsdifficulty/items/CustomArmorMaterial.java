package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum CustomArmorMaterial implements ArmorMaterial {
	TATTERED(
		"tattered",
		5,
		new int[]{ 1, 2, 3, 1 },
		15,
		SoundEvents.ARMOR_EQUIP_LEATHER,
		0.0f,
		0.0f,
		()->Ingredient.of( MajruszsDifficulty.CLOTH_ITEM.get() )
	),
	ENDERIUM(
		"enderium",
		39,
		new int[]{ 4, 6, 8, 4 },
		15,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		4.0f,
		0.1f,
		()->Ingredient.of( MajruszsDifficulty.ENDERIUM_INGOT_ITEM.get() )
	);

	private static final int[] MAX_DAMAGE_ARRAY = new int[]{ 13, 15, 16, 11 };
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue< Ingredient > repairMaterial;

	CustomArmorMaterial( String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness,
		float knockbackResistance, Supplier< Ingredient > repairMaterial
	) {
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmountArray;
		this.enchantability = enchantability;
		this.soundEvent = soundEvent;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairMaterial = new LazyLoadedValue<>( repairMaterial );
	}

	@Override
	public int getDurabilityForSlot( EquipmentSlot slot ) {
		return this.maxDamageFactor * MAX_DAMAGE_ARRAY[ switch( slot ) {
			case FEET -> 0;
			case LEGS -> 1;
			case CHEST -> 2;
			case HEAD -> 3;
			default -> 0;
		} ];
	}

	@Override
	public int getDefenseForSlot( EquipmentSlot slot ) {
		return this.damageReductionAmountArray[ switch( slot ) {
			case FEET -> 0;
			case LEGS -> 1;
			case CHEST -> 2;
			case HEAD -> 3;
			default -> 0;
		} ];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.soundEvent;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}

package com.majruszs_difficulty.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/** Mod custom armor materials. */
public enum CustomArmorMaterial implements IArmorMaterial {
	HERMES( "hermes", 10, new int[]{ 2, 5, 6, 2 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, ()->{
		return Ingredient.fromItems( Items.FEATHER );
	} );

	private static final int[] MAX_DAMAGE_ARRAY = new int[]{ 13, 15, 16, 11 };
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyValue< Ingredient > repairMaterial;

	CustomArmorMaterial( String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent,
		float toughness, float knockbackResistance, Supplier< Ingredient > repairMaterial
	) {
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmountArray;
		this.enchantability = enchantability;
		this.soundEvent = soundEvent;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairMaterial = new LazyValue<>( repairMaterial );
	}

	@Override
	public int getDurability( EquipmentSlotType equipmentSlotType ) {
		return this.maxDamageFactor * MAX_DAMAGE_ARRAY[ equipmentSlotType.getIndex() ];
	}

	@Override
	public int getDamageReductionAmount( EquipmentSlotType equipmentSlotType ) {
		return this.damageReductionAmountArray[ equipmentSlotType.getIndex() ];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.getValue();
	}

	@Override
	@OnlyIn( Dist.CLIENT )
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

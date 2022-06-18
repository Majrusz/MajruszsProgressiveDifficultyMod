package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/** Mod custom armor materials. */
public enum CustomArmorMaterial implements ArmorMaterial {
	HERMES( "hermes", 10, new int[]{
		2,
		5,
		6,
		2
	}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, ()->{
		return Ingredient.of( Items.FEATHER );
	} ), END( "end", 39, new int[]{
		4,
		6,
		8,
		4
	}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5f, 0.1f, ()->{
		return Ingredient.of( Registries.END_INGOT.get() );
	} );

	private static final int[] MAX_DAMAGE_ARRAY = new int[]{
		13,
		15,
		16,
		11
	};
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue< Ingredient > repairMaterial;

	CustomArmorMaterial( String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance,
		Supplier< Ingredient > repairMaterial
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
	public int getDurabilityForSlot( EquipmentSlot equipmentSlotType ) {
		return this.maxDamageFactor * MAX_DAMAGE_ARRAY[ equipmentSlotType.getIndex() ];
	}

	@Override
	public int getDefenseForSlot( EquipmentSlot equipmentSlotType ) {
		return this.damageReductionAmountArray[ equipmentSlotType.getIndex() ];
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

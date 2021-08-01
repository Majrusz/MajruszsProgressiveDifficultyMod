package com.majruszs_difficulty.items;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.ArrayList;
import java.util.List;

/** Armor item that updates attributes after config was loaded or reloaded. */
public abstract class AttributeArmorItem extends ArmorItem {
	protected static final List< AttributeArmorItem > ALL_ATTRIBUTE_ITEMS = new ArrayList<>();

	public AttributeArmorItem( ArmorMaterial material, EquipmentSlot slot, Properties properties ) {
		super( material, slot, properties );

		ALL_ATTRIBUTE_ITEMS.add( this );
	}

	/** Updates attribute values in all AttributeArmorItem class items. */
	public static void updateAllItemsAttributes() {
		ALL_ATTRIBUTE_ITEMS.forEach( AttributeArmorItem::updateSingleItem );
	}

	/** Copies all attributes and adds a new ones to a single item. */
	private static void updateSingleItem( AttributeArmorItem item ) {
		ImmutableMultimap.Builder< Attribute, AttributeModifier > builder = ImmutableMultimap.builder();
		builder.putAll( item.defaultModifiers );
		item.updateAttributes( builder );
		item.defaultModifiers = builder.build();
	}

	/** Called whenever attributes should be updated. */
	protected abstract void updateAttributes( ImmutableMultimap.Builder< Attribute, AttributeModifier > builder );
}

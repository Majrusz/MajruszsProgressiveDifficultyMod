package com.majruszs_difficulty.items;

import com.google.common.collect.ImmutableMultimap;
import com.mlib.MajruszLibrary;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

import java.util.ArrayList;
import java.util.List;

/** Armor item that updates attributes after config was loaded or reloaded. */
public abstract class AttributeArmorItem extends ArmorItem {
	protected static final List< AttributeArmorItem > ALL_ATTRIBUTE_ITEMS = new ArrayList<>();

	public AttributeArmorItem( IArmorMaterial material, EquipmentSlotType slot, Properties properties ) {
		super( material, slot, properties );

		ALL_ATTRIBUTE_ITEMS.add( this );
	}

	/** Updates attribute values in all AttributeArmorItem class items. */
	public static void updateAllItemsAttributes() {
		MajruszLibrary.LOGGER.info( "COOOL!" );

		ALL_ATTRIBUTE_ITEMS.forEach( AttributeArmorItem::updateSingleItem );
	}

	/** Copies all attributes and adds a new ones to a single item. */
	private static void updateSingleItem( AttributeArmorItem item ) {
		ImmutableMultimap.Builder< Attribute, AttributeModifier > builder = ImmutableMultimap.builder();
		builder.putAll( item.field_234656_m_ );
		item.updateAttributes( builder );
		item.field_234656_m_ = builder.build();
	}

	/** Called whenever attributes should be updated. */
	protected abstract void updateAttributes( ImmutableMultimap.Builder< Attribute, AttributeModifier > builder );
}

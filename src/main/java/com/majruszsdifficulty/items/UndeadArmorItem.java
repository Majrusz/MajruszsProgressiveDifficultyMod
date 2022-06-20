package com.majruszsdifficulty.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

/** Helper for anything related to Undead Army armor because in reality it is just coloured leather armor with proper NBT tags. */
public class UndeadArmorItem {
	public static final Predicate< ItemStack > IS_SET_ITEM = stack->stack.getTag() != null && stack.getTag().getBoolean( "UndeadArmySet" );
	public static final String BOOTS_ID = "majruszsdifficulty.items.undead_boots";
	public static final String CHESTPLATE_ID = "majruszsdifficulty.items.undead_chestplate";
	public static final String HELMET_ID = "majruszsdifficulty.items.undead_helmet";
	public static final String LEGGINGS_ID = "majruszsdifficulty.items.undead_leggings";
	public static final String NBT_TAG = "UndeadArmySet";
	public static final int ARMOR_COLOR = 0x92687b;

	public static ItemStack constructItem( String id ) {
		ItemData itemData = getData( id );
		ItemStack itemStack = new ItemStack( itemData.item );

		CompoundTag mainTag = itemStack.getOrCreateTag();
		mainTag.putBoolean( NBT_TAG, true );
		itemStack.setTag( mainTag );

		CompoundTag displayTag = itemStack.getOrCreateTagElement( "display" );
		displayTag.putLong( "color", ARMOR_COLOR );
		displayTag.putString( "Name", "{\"translate\":\"" + id + "\",\"italic\":false}" );
		itemStack.addTagElement( "display", displayTag );

		return itemStack;
	}

	public static ItemData getData( String id ) {
		return switch( id ) {
			case BOOTS_ID -> new ItemData( Items.LEATHER_BOOTS, EquipmentSlot.FEET );
			case CHESTPLATE_ID -> new ItemData( Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST );
			case HELMET_ID -> new ItemData( Items.LEATHER_HELMET, EquipmentSlot.HEAD );
			case LEGGINGS_ID -> new ItemData( Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS );
			default -> new ItemData( null, null );
		};
	}

	public record ItemData( Item item, EquipmentSlot slot ) {}
}

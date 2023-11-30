package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class ItemSetRequirement {
	private final Supplier< ? extends Item > item;
	private final EquipmentSlot slot;

	public static ItemSetRequirement of( Supplier< ? extends Item > item, EquipmentSlot slot ) {
		return new ItemSetRequirement( item, slot );
	}

	public boolean check( LivingEntity entity ) {
		return entity.getItemBySlot( this.slot ).is( this.item.get() );
	}

	public boolean is( ItemStack itemStack ) {
		return itemStack.is( this.item.get() );
	}

	public Component getComponent() {
		return this.item.get().getDescription();
	}

	public Component toComponent( ItemSet itemSet, List< ItemSetRequirement > requirementsMet ) {
		return TextHelper.translatable( "majruszsdifficulty.item_sets.item", this.getComponent() )
			.withStyle( requirementsMet.contains( this ) ? itemSet.getFormatting() : new ChatFormatting[]{ ChatFormatting.DARK_GRAY } );
	}

	private ItemSetRequirement( Supplier< ? extends Item > item, EquipmentSlot slot ) {
		this.item = item;
		this.slot = slot;
	}
}

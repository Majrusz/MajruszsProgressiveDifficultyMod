package com.majruszsdifficulty.events;

import com.majruszlibrary.events.OnItemAttributeTooltip;
import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class OnBleedingTooltip {
	public final ItemStack itemStack;
	public final int amplifier;
	private final OnItemAttributeTooltip data;

	public static Event< OnBleedingTooltip > listen( Consumer< OnBleedingTooltip > consumer ) {
		return Events.get( OnBleedingTooltip.class ).add( consumer );
	}

	public OnBleedingTooltip( OnItemAttributeTooltip data, int amplifier ) {
		this.itemStack = data.itemStack;
		this.amplifier = amplifier;
		this.data = data;
	}

	public void addItem( double chance ) {
		MutableComponent component = TextHelper.translatable( "effect.majruszsdifficulty.bleeding.item_tooltip", TextHelper.percent( ( float )chance ), TextHelper.toRoman( this.amplifier + 1 ) )
			.withStyle( ChatFormatting.DARK_GREEN );

		this.data.add( EquipmentSlot.MAINHAND, component );
	}

	public void addArmor( EquipmentSlot slot, double chanceMultiplier ) {
		MutableComponent component = TextHelper.translatable( "effect.majruszsdifficulty.bleeding.armor_tooltip", TextHelper.minPrecision( chanceMultiplier, 3 ) )
			.withStyle( ChatFormatting.BLUE );

		this.data.add( slot, component );
	}
}

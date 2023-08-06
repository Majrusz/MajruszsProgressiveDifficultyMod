package com.majruszsdifficulty.gamemodifiers.contexts;

import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.Contexts;
import com.mlib.contexts.OnItemAttributeTooltip;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class OnBleedingTooltip {
	public static Context< Data > listen( Consumer< Data > consumer ) {
		return Contexts.get( Data.class ).add( consumer );
	}

	public static Data dispatch( ItemStack itemStack, int amplifier ) {
		return Contexts.get( Data.class ).dispatch( new Data( itemStack, amplifier ) );
	}

	public static class Data extends OnItemAttributeTooltip.Data {
		final int amplifier;

		public Data( ItemStack itemStack, int amplifier ) {
			super( itemStack );

			this.amplifier = amplifier;
		}

		public void addItem( double chance ) {
			this.add( EquipmentSlot.MAINHAND, Component.translatable( "effect.majruszsdifficulty.bleeding.item_tooltip", TextHelper.percent( ( float )chance ), TextHelper.toRoman( this.amplifier + 1 ) )
				.withStyle( ChatFormatting.DARK_GREEN ) );
		}

		public void addArmor( EquipmentSlot slot, double chanceMultiplier ) {
			this.add( slot, Component.translatable( "effect.majruszsdifficulty.bleeding.armor_tooltip", TextHelper.minPrecision( chanceMultiplier ) )
				.withStyle( ChatFormatting.BLUE ) );
		}

		public void addAll( OnItemAttributeTooltip.Data data ) {
			this.components.forEach( ( slot, components )->components.forEach( component->data.add( slot, component ) ) );
		}
	}
}

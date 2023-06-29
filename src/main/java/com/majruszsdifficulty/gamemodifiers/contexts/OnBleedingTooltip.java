package com.majruszsdifficulty.gamemodifiers.contexts;

import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.gamemodifiers.Context;
import com.mlib.gamemodifiers.Contexts;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
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

	public static Data dispatch( ItemStack itemStack ) {
		return Contexts.get( Data.class ).dispatch( new Data( itemStack ) );
	}

	public static class Data extends OnItemAttributeTooltip.Data {
		public Data( ItemStack itemStack ) {
			super( itemStack );
		}

		public void add( double chance, int amplifier ) {
			this.add( EquipmentSlot.MAINHAND, Component.translatable( "effect.majruszsdifficulty.bleeding.item_tooltip", TextHelper.percent( ( float )chance ), TextHelper.toRoman( amplifier + 1 ) )
				.withStyle( ChatFormatting.DARK_GREEN ) );
		}

		public void add( double chance ) {
			this.add( chance, BleedingEffect.getAmplifier() );
		}

		public void addAll( OnItemAttributeTooltip.Data data ) {
			this.components.forEach( ( slot, components )->components.forEach( component->data.add( slot, component ) ) );
		}
	}
}

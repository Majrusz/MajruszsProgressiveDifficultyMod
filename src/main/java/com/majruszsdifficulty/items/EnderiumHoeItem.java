package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.contexts.OnFarmlandTillCheck;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Rarity;

public class EnderiumHoeItem extends HoeItem {
	public EnderiumHoeItem() {
		super( CustomItemTier.END, -5, 0.0f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@AutoInstance
	public static class IncreaseTillArea {
		static final String ATTRIBUTE_ID = "item.majruszsdifficulty.enderium_hoe.effect";

		public IncreaseTillArea() {
			new OnFarmlandTillCheck.Context( OnFarmlandTillCheck.INCREASE_AREA )
				.addCondition( OnFarmlandTillCheck.IS_NOT_CROUCHING )
				.addCondition( data->data.itemStack.getItem() instanceof EnderiumHoeItem );

			new OnItemAttributeTooltip.Context( this::addTooltip )
				.addCondition( data->data.item instanceof EnderiumHoeItem );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			data.add( EquipmentSlot.MAINHAND, new TranslatableComponent( ATTRIBUTE_ID ).withStyle( ChatFormatting.DARK_GREEN ) );
		}
	}
}

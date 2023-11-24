package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.events.OnItemEaten;
import com.majruszlibrary.events.OnItemTooltip;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class BleedingAppleProtection {
	private static final List< Item > ITEMS = List.of( Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE );

	static {
		OnItemEaten.listen( BleedingAppleProtection::cancelBleeding )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->BleedingConfig.CAN_BE_CURED_WITH_GOLDEN_APPLES )
			.addCondition( data->ITEMS.contains( data.itemStack.getItem() ) );

		OnItemTooltip.listen( BleedingAppleProtection::addTooltip )
			.addCondition( OnItemTooltip::isAdvanced )
			.addCondition( data->BleedingConfig.CAN_BE_CURED_WITH_GOLDEN_APPLES )
			.addCondition( data->ITEMS.contains( data.itemStack.getItem() ) );
	}

	private static void cancelBleeding( OnItemEaten data ) {
		data.entity.removeEffect( MajruszsDifficulty.Effects.BLEEDING.get() );
	}

	private static void addTooltip( OnItemTooltip data ) {
		data.components.add( TextHelper.empty() );
		data.components.add( TextHelper.translatable( "effect.majruszsdifficulty.bleeding.item_consumed" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.bandage.effect" ).withStyle( ChatFormatting.BLUE ) );
	}
}

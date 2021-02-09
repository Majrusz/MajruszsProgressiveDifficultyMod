package com.majruszs_difficulty.events;

import com.majruszs_difficulty.items.EndAxeItem;
import com.majruszs_difficulty.items.EndHoeItem;
import com.majruszs_difficulty.items.EndPickaxeItem;
import com.majruszs_difficulty.items.EndShovelItem;
import com.mlib.TimeConverter;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Getting Haste effect when destroying blocks with End tools. */
@Mod.EventBusSubscriber
public class HasteOnDestroyingBlocks {
	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		PlayerEntity player = event.getPlayer();
		ItemStack tool = player.getHeldItemMainhand();
		if( tool.isEmpty() || !isEndTool( tool.getItem() ) )
			return;

		EffectHelper.applyEffectIfPossible( player, Effects.HASTE, TimeConverter.secondsToTicks( 5.0 ), 0 );
	}

	public static IFormattableTextComponent getTooltip() {
		return new TranslationTextComponent( "majruszs_difficulty.effects.haste_tooltip" ).mergeStyle( TextFormatting.GRAY );
	}

	/** Checks whether tool is made from end ingredients. */
	private static boolean isEndTool( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndAxeItem || item instanceof EndPickaxeItem || item instanceof EndShovelItem;
	}
}

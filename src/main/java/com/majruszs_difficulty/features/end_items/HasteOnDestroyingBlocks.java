package com.majruszs_difficulty.features.end_items;

import com.mlib.TimeConverter;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Gives Haste effect whenever player destroys block with End items. */
@Mod.EventBusSubscriber
public class HasteOnDestroyingBlocks {
	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		PlayerEntity player = event.getPlayer();
		ItemStack tool = player.getHeldItemMainhand();

		if( !tool.isEmpty() && EndItems.isEndItem( tool.getItem() ) )
			EffectHelper.applyEffectIfPossible( player, Effects.HASTE, TimeConverter.secondsToTicks( 5.0 ), 0 );
	}
}

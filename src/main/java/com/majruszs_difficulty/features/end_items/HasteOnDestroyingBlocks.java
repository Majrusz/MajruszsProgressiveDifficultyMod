package com.majruszs_difficulty.features.end_items;

import com.mlib.TimeConverter;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Gives Haste effect whenever player destroys block with End items. */
@Mod.EventBusSubscriber
public class HasteOnDestroyingBlocks {
	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		Player player = event.getPlayer();
		ItemStack tool = player.getMainHandItem();

		if( !tool.isEmpty() && EndItems.isEndItem( tool.getItem() ) )
			EffectHelper.applyEffectIfPossible( player, MobEffects.DIG_SPEED, TimeConverter.secondsToTicks( 5.0 ), 0 );
	}
}

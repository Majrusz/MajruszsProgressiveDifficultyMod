package com.majruszsdifficulty.features.end_items;

import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.entities.EntityHelper;
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

		if( !tool.isEmpty() && EndItems.isEndItem( tool.getItem() ) && !EntityHelper.isOnCreativeMode( player ) )
			EffectHelper.applyEffectIfPossible( player, MobEffects.DIG_SPEED, Utility.secondsToTicks( 5.0 ), 0 );
	}
}

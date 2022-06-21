package com.majruszsdifficulty.features;

import com.majruszsdifficulty.items.*;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.entities.EntityHelper;
import com.mlib.features.FarmlandTiller;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Class with common functions/variables for end items special functionalities. */
@Mod.EventBusSubscriber
public class EnderiumItems {
	static {
		FarmlandTiller.registerList.add( new FarmlandTiller.Register() {
			@Override
			public boolean shouldBeExecuted( ServerLevel serverLevel, Player player, ItemStack itemStack ) {
				return itemStack.getItem() instanceof HoeItem;
			}
		} );
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		Player player = event.getPlayer();
		ItemStack tool = player.getMainHandItem();

		if( !tool.isEmpty() && EnderiumItems.isEnderiumTool( tool.getItem() ) && !EntityHelper.isOnCreativeMode( player ) )
			EffectHelper.applyEffectIfPossible( player, MobEffects.DIG_SPEED, Utility.secondsToTicks( 5.0 ), 0 );
	}

	public static boolean isEnderiumTool( Item item ) {
		return item instanceof EnderiumHoeItem || item instanceof EnderiumAxeItem || item instanceof EnderiumPickaxeItem || item instanceof EnderiumShovelItem;
	}

	public static class Keys {
		public static final String HASTE_TOOLTIP = "majruszsdifficulty.enderium_items.haste_tooltip";
		public static final String TILL_TOOLTIP = "majruszsdifficulty.enderium_items.till_tooltip";
	}
}

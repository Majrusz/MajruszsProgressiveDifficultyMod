package com.majruszsdifficulty;

import com.majruszsdifficulty.items.EnderiumAxeItem;
import com.majruszsdifficulty.items.EnderiumHoeItem;
import com.majruszsdifficulty.items.EnderiumPickaxeItem;
import com.majruszsdifficulty.items.EnderiumShovelItem;
import com.mlib.Utility;
import com.mlib.entities.EntityHelper;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Class with common functions/variables for end items special functionalities. */
@Mod.EventBusSubscriber
@Deprecated
public class EnderiumItems {
	/*static {
		FarmlandTiller.registerList.add( new FarmlandTiller.Register() {
			@Override
			public boolean shouldBeExecuted( ServerLevel serverLevel, Player player, ItemStack itemStack ) {
				return itemStack.getItem() instanceof EnderiumHoeItem;
			}
		} );
	}*/

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		Player player = event.getPlayer();
		ItemStack tool = player.getMainHandItem();

		if( !tool.isEmpty() && EnderiumItems.isEnderiumTool( tool.getItem() ) && !EntityHelper.isOnCreativeMode( player ) )
			MobEffectHelper.tryToApply( player, MobEffects.DIG_SPEED, Utility.secondsToTicks( 5.0 ), 0 );
	}

	public static boolean isEnderiumTool( Item item ) {
		return item instanceof EnderiumHoeItem || item instanceof EnderiumAxeItem || item instanceof EnderiumPickaxeItem || item instanceof EnderiumShovelItem;
	}
}

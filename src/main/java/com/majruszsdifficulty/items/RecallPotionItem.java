package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.items.ItemHelper;
import com.mlib.levels.LevelHelper;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** Potion that will teleport the player to spawn/bed position after drinking it. */
public class RecallPotionItem extends Item {
	static final String TOOLTIP_ID = "item.majruszsdifficulty.recall_potion.effect";

	public RecallPotionItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).stacksTo( 16 ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		return ItemUtils.startUsingInstantly( world, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity livingEntity ) {
		if( livingEntity instanceof Player player ) {
			ItemHelper.consumeItemOnUse( itemStack, player );
		}
		if( livingEntity instanceof ServerPlayer serverPlayer ) {
			CriteriaTriggers.CONSUME_ITEM.trigger( serverPlayer, itemStack );
			LevelHelper.teleportToSpawnPosition( serverPlayer );
			MobEffectHelper.tryToApply( serverPlayer, MobEffects.CONFUSION, Utility.secondsToTicks( 7.0 ), 1 );
		}

		return itemStack;
	}

	@Override
	public UseAnim getUseAnimation( ItemStack itemStack ) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration( ItemStack itemStack ) {
		return 32;
	}

	@Override
	public boolean isFoil( ItemStack itemStack ) {
		return true;
	}

	@Override
	public void appendHoverText( ItemStack itemStack, @Nullable Level level, List< Component > components, TooltipFlag flag ) {
		components.add( CommonComponents.EMPTY );
		components.add( Component.translatable( "potion.whenDrank" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		components.add( Component.translatable( TOOLTIP_ID ).withStyle( ChatFormatting.BLUE ) );
	}
}

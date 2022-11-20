package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.items.ItemHelper;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class BadOmenPotionItem extends Item {
	public BadOmenPotionItem() {
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
			MobEffectHelper.tryToStackAmplifier( serverPlayer, MobEffects.BAD_OMEN, Utility.minutesToTicks( 30.0 ), 0, 5 );
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
}

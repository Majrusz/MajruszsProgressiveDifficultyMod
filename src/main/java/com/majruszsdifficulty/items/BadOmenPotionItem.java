package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.items.ItemHelper;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.text.TextHelper;
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

public class BadOmenPotionItem extends Item {
	static final String TOOLTIP_ID = "item.majruszsdifficulty.bad_omen_potion.effect";

	public BadOmenPotionItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).stacksTo( 16 ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		return ItemUtils.startUsingInstantly( world, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity entity ) {
		if( entity instanceof Player player ) {
			ItemHelper.consumeItemOnUse( itemStack, player );
		}
		if( entity instanceof ServerPlayer serverPlayer ) {
			CriteriaTriggers.CONSUME_ITEM.trigger( serverPlayer, itemStack );
			MobEffectHelper.tryToStackAmplifier( serverPlayer, MobEffects.BAD_OMEN, Utility.minutesToTicks( 90.0 ), 0, 5 );
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
		String amplifier = TextHelper.signed( 1 );

		components.add( CommonComponents.EMPTY );
		components.add( Component.translatable( "potion.whenDrank" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		components.add( Component.translatable( TOOLTIP_ID, amplifier ).withStyle( ChatFormatting.BLUE ) );
	}
}

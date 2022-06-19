package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.mlib.LevelHelper;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.entities.EntityHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** Potion that will teleport the player to spawn/bed position after drinking it. */
public class RecallPotionItem extends Item {
	private static final String TOOLTIP_TRANSLATION_KEY = "item.majruszsdifficulty.recall_potion.item_tooltip";

	public RecallPotionItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).stacksTo( 16 ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		return ItemUtils.startUsingInstantly( world, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity livingEntity ) {
		if( livingEntity instanceof Player player )
		{
			ItemHelper.consumeItemOnUse( itemStack, player );

			if( player instanceof ServerPlayer serverPlayer ) {
				CriteriaTriggers.CONSUME_ITEM.trigger( serverPlayer, itemStack );
				if( level instanceof ServerLevel serverLevel )
					teleportToSpawnPosition( serverPlayer, serverLevel );
			}
		}

		return itemStack;
	}

	protected void teleportToSpawnPosition( ServerPlayer player, ServerLevel level ) {
		BlockPos spawnPosition = LevelHelper.getSpawnPosition( player, level );
		player.randomTeleport( spawnPosition.getX() + 0.5, spawnPosition.getY() + 1.0, spawnPosition.getZ() + 0.5, true );
		EffectHelper.applyEffectIfPossible( player, MobEffects.CONFUSION, Utility.secondsToTicks( 6.0 ), 0 );
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
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY );
	}

	@Override
	public boolean isFoil( ItemStack itemStack ) {
		return true;
	}
}

package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making attack with tools inflict bleeding on enemies. */
public class SharpItemBleedingOnAttack extends WhenDamagedApplyBleedingBase {
	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( attacker != null ) {
			ItemStack heldItemStack = attacker.getHeldItemMainhand();
			Item heldItem = heldItemStack.getItem();

			boolean attackerHasSharpItem = heldItem instanceof ToolItem || heldItem instanceof TridentItem || heldItem instanceof SwordItem || heldItem instanceof ShearsItem;

			return attackerHasSharpItem && super.shouldBeExecuted( attacker, target, damageSource );
		}

		return false;
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.SHARP_ITEM_BLEEDING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.SHARP_ITEM_BLEEDING );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.SHARP_ITEM_BLEEDING );
	}
}
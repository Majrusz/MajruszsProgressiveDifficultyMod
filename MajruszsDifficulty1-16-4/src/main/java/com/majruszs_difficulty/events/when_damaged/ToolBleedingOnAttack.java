package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.effects.BleedingEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making attack with tools inflict bleeding on enemies. */
public class ToolBleedingOnAttack extends WhenDamagedApplyEffectBase {
	public ToolBleedingOnAttack() {
		super( GameState.Mode.NORMAL, true, BleedingEffect.instance );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean attackerHasTool = attacker != null && attacker.getHeldItemMainhand().getItem() instanceof ToolItem;

		return attackerHasTool && BleedingEffect.mayBleed( target ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.TOOL_BLEEDING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.TOOL_BLEEDING );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.TOOL_BLEEDING );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return BleedingEffect.getAmplifier();
	}
}
package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.items.EndSwordItem;
import com.mlib.TimeConverter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making attack with tools inflict bleeding on enemies. */
public class EndSwordLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "EndSwordLevitation";
	private static final String CONFIG_COMMENT = "Sneaking while attacking with End Sword inflicts levitation.";

	public EndSwordLevitationOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 4.0, GameState.State.NORMAL, false, Effects.LEVITATION, false, true, 0, TimeConverter.secondsToTicks( 60.0 ) );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( attacker != null ) {
			ItemStack heldItemStack = attacker.getHeldItemMainhand();
			return attacker.isSneaking() && heldItemStack.getItem() instanceof EndSwordItem && super.shouldBeExecuted( attacker, target, damageSource );
		}

		return false;
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
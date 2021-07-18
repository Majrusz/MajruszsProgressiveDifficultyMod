package com.majruszs_difficulty.features.end_items.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.features.end_items.EndItems;
import com.majruszs_difficulty.features.when_damaged.WhenDamagedApplyStackableEffectBase;
import com.mlib.TimeConverter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Makes certain End Tools have a chance to apply Levitation. */
public class EndToolsLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "EndToolsLevitation";
	private static final String CONFIG_COMMENT = "Makes End Pickaxe and End Shovel have a chance to inflict Levitation effect.";

	public EndToolsLevitationOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.75, 4.0, GameState.State.NORMAL, false, Effects.LEVITATION, false, true, 0,
			TimeConverter.secondsToTicks( 60.0 )
		);
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( attacker != null ) {
			ItemStack heldItemStack = attacker.getHeldItemMainhand();
			return !attacker.isSneaking() && EndItems.canInflictLevitation( heldItemStack.getItem() ) && super.shouldBeExecuted( attacker, target,
				damageSource
			);
		}

		return false;
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
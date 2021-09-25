package com.majruszs_difficulty.features.end_items.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.features.end_items.EndItems;
import com.majruszs_difficulty.features.when_damaged.WhenDamagedApplyStackableEffectBase;
import com.mlib.TimeConverter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/** Makes certain End Tools have a chance to apply Levitation. */
public class EndToolsLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "EndToolsLevitation";
	private static final String CONFIG_COMMENT = "Makes End Pickaxe and End Shovel have a chance to inflict Levitation effect.";

	public EndToolsLevitationOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.75, 4.0, GameState.State.NORMAL, false, MobEffects.LEVITATION, false, true, 0,
			TimeConverter.secondsToTicks( 60.0 )
		);
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( attacker != null ) {
			ItemStack heldItemStack = attacker.getMainHandItem();
			return !attacker.isShiftKeyDown() && EndItems.canInflictLevitation( heldItemStack.getItem() ) && super.shouldBeExecuted( attacker, target,
				damageSource
			) && attacker.equals( damageSource.getDirectEntity() );
		}

		return false;
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
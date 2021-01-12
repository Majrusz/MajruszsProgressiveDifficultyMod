package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making entities who are able to 'bite' inflict bleeding on enemies. */
public class BiteBleedingOnAttack extends WhenDamagedApplyBleedingBase {
	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean mayBite = attacker instanceof AnimalEntity || attacker instanceof ZombieEntity || attacker instanceof SpiderEntity;

		return mayBite && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.BITE_BLEEDING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.BITE_BLEEDING );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.BITE_BLEEDING );
	}
}
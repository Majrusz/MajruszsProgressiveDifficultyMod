package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Makes all Creepers ignite every nearby Creeper when exploding. */
public class IgniteCreeperOnHurt extends WhenDamagedBase {
	private static final String CONFIG_NAME = "CreeperIgnite";
	private static final String CONFIG_COMMENT = "Makes all Creepers ignite every nearby Creeper when exploding.";

	public IgniteCreeperOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		CreeperEntity creeper = ( CreeperEntity )target;
		creeper.ignite();
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof CreeperEntity && target instanceof CreeperEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
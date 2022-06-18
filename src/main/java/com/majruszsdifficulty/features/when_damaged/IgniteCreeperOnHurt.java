package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
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
		Creeper creeper = ( Creeper )target;
		creeper.ignite();
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof Creeper && target instanceof Creeper && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
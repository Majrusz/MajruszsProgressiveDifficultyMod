package com.majruszsdifficulty.features.when_damaged;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import javax.annotation.Nullable;

/** Making entities who are able to 'bite' inflict bleeding on enemies. */
public class BiteBleedingOnAttack extends WhenDamagedApplyBleedingBaseOld {
	private static final String CONFIG_NAME = "BiteBleeding";
	private static final String CONFIG_COMMENT = "Animals, zombies and spiders inflict bleeding.";

	public BiteBleedingOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean mayBite = attacker instanceof Animal || attacker instanceof Zombie || attacker instanceof Spider;
		mayBite &= !( attacker instanceof Llama );

		return mayBite && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
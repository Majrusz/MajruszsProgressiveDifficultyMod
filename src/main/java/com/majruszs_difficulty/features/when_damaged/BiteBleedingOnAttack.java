package com.majruszs_difficulty.events.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/** Making entities who are able to 'bite' inflict bleeding on enemies. */
public class BiteBleedingOnAttack extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "BiteBleeding";
	private static final String CONFIG_COMMENT = "Animals, zombies and spiders inflict bleeding.";

	public BiteBleedingOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean mayBite = attacker instanceof AnimalEntity || attacker instanceof ZombieEntity || attacker instanceof SpiderEntity;
		mayBite &= !( attacker instanceof LlamaEntity );

		return mayBite && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
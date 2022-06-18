package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.entities.ParasiteEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Makes parasite have a chance to infest the target. */
public class ParasiteInfestOnAttack extends ChanceWhenDamagedBase {
	private static final String CONFIG_NAME = "ParasiteInfest";
	private static final String CONFIG_COMMENT = "Makes parasite have a chance to infest the target.";

	public ParasiteInfestOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, GameState.State.NORMAL, false );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( attacker == null || !tryChance( target ) )
			return;

		ParasiteEntity.spawnEffects( ( ServerLevel )target.level, attacker.blockPosition() );
		attacker.remove( Entity.RemovalReason.DISCARDED );
		Instances.INFESTED.applyTo( target );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof ParasiteEntity && attacker.equals( damageSource.getDirectEntity() ) && Instances.INFESTED.canBeAppliedTo(
			target ) && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
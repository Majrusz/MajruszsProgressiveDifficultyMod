package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.ParasiteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
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

		ParasiteEntity.spawnEffects( ( ServerWorld )target.world, attacker.getPosition() );
		attacker.remove();
		Instances.INFESTED.applyTo( target );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof ParasiteEntity && attacker.equals( damageSource.getImmediateSource() ) && Instances.INFESTED.canBeAppliedTo(
			target ) && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
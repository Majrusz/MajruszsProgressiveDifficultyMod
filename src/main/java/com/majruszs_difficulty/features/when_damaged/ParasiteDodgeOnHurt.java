package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.entities.ParasiteEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Makes parasite have a chance to teleport somewhere nearby when damaged. */
public class ParasiteDodgeOnHurt extends ChanceWhenDamagedBase {
	private static final String CONFIG_NAME = "ParasiteDodge";
	private static final String CONFIG_COMMENT = "Gives parasite a chance to teleport somewhere nearby when damaged.";

	public ParasiteDodgeOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.4, GameState.State.NORMAL, false );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( !tryChance( target ) )
			return;

		ServerLevel world = ( ServerLevel )target.level;
		if( MajruszsHelper.teleportNearby( target, world, 3.0, 4.0 ) )
			ParasiteEntity.spawnEffects( world, target.blockPosition() );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return target instanceof ParasiteEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
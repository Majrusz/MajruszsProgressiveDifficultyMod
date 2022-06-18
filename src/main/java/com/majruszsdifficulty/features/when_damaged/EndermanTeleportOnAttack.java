package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Makes Enderman attacks have a chance to teleport entity. */
public class EndermanTeleportOnAttack extends ChanceWhenDamagedBase {
	private static final String CONFIG_NAME = "EndermanTeleport";
	private static final String CONFIG_COMMENT = "Enderman attack teleports the player somewhere nearby.";

	public EndermanTeleportOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, GameState.State.MASTER, true );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( !tryChance( target ) )
			return;

		ServerLevel world = ( ServerLevel )target.level;
		if( !MajruszsHelper.teleportNearby( target, world, 5.0, 10.0 ) )
			return;

		world.playSound( null, target.xo, target.yo, target.zo, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f );
		world.sendParticles( ParticleTypes.PORTAL, target.xo, target.getY( 0.5 ), target.zo, 10, 0.25, 0.25, 0.25, 0.1 );
		if( target instanceof ServerPlayer )
			Registries.BASIC_TRIGGER.trigger( ( ServerPlayer )target, "enderman_teleport_attack" );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof EnderMan && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
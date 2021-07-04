package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
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

		ServerWorld world = ( ServerWorld )target.world;
		if( !MajruszsHelper.teleportNearby( target, world, 5.0, 10.0 ) )
			return;

		world.playSound( null, target.prevPosX, target.prevPosY, target.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f,
			1.0f
		);
		world.spawnParticle( ParticleTypes.PORTAL, target.prevPosX, target.getPosYHeight( 0.5 ), target.prevPosZ, 10, 0.25, 0.25, 0.25, 0.1 );
		if( target instanceof ServerPlayerEntity )
			Instances.SIMPLE_TRIGGER.trigger( ( ServerPlayerEntity )target, "enderman_teleport_attack" );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof EndermanEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
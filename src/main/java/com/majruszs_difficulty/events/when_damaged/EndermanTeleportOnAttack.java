package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Makes Enderman attacks have a chance to teleport entity. */
public class EndermanTeleportOnAttack extends WhenDamagedBase {
	private static final String CONFIG_NAME = "EndermanTeleport";
	private static final String CONFIG_COMMENT = "Spider inflicts poison.";

	public EndermanTeleportOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, GameState.State.MASTER, true );
	}

	@Override
	public void whenDamaged( LivingEntity target ) {
		ServerWorld world = ( ServerWorld )target.world;

		Vector3d newPosition = Random.getRandomVector3d( -10.0, 10.0, 0.0, 0.0, -10.0, 10.0 )
			.add( target.getPositionVec() );

		double y = world.getHeight( Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ( int )newPosition.x, ( int )newPosition.z ) + 1;

		if( y < 5 )
			return;

		world.playSound( null, target.prevPosX, target.prevPosY, target.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f );
		world.spawnParticle( ParticleTypes.PORTAL, target.prevPosX, target.getPosYHeight( 0.5 ), target.prevPosZ, 10, 0.25, 0.25, 0.25, 0.1 );
		target.attemptTeleport( newPosition.x, y, newPosition.z, true );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof EndermanEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
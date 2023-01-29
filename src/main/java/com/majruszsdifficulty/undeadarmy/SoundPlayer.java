package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.effects.SoundHandler;
import com.mlib.math.VectorHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

record SoundPlayer( UndeadArmy undeadArmy ) implements IComponent {
	static final Supplier< Float > PITCH = SoundHandler.randomized( 1.0f, 0.95f, 1.05f );
	static final SoundHandler APPROACHING = new SoundHandler( Registries.UNDEAD_ARMY_APPROACHING, SoundSource.AMBIENT, SoundHandler.randomized( 0.25f ), PITCH );
	static final SoundHandler WAVE_STARTED = new SoundHandler( Registries.UNDEAD_ARMY_WAVE_STARTED, SoundSource.NEUTRAL, SoundHandler.randomized( 64.0f ), PITCH );

	@Override
	public void onStart() {
		APPROACHING.play( this.undeadArmy.level, VectorHelper.vec3( this.undeadArmy.positionToAttack ) );
	}

	@Override
	public void onPhaseChanged() {
		if( this.undeadArmy.phase.state == Phase.State.WAVE_ONGOING ) {
			Vec3 position = new Vec3(
				this.undeadArmy.positionToAttack.getX() + this.undeadArmy.direction.x * 50,
				0,
				this.undeadArmy.positionToAttack.getZ() + this.undeadArmy.direction.z * 50
			);

			this.undeadArmy.participants.forEach( player->WAVE_STARTED.send( player, new Vec3( position.x, player.getY(), position.z ) ) );
		}
	}
}

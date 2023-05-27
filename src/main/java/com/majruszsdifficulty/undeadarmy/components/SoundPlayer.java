package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import com.mlib.effects.SoundHandler;
import com.mlib.math.AnyPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

record SoundPlayer( UndeadArmy undeadArmy ) implements IComponent {
	static final Supplier< Float > PITCH = SoundHandler.randomized( 1.0f, 0.95f, 1.05f );
	static final SoundHandler APPROACHING = new SoundHandler( Registries.UNDEAD_ARMY_APPROACHING, SoundSource.AMBIENT, SoundHandler.randomized( 0.25f ), PITCH );
	static final SoundHandler WAVE_STARTED = new SoundHandler( Registries.UNDEAD_ARMY_WAVE_STARTED, SoundSource.NEUTRAL, SoundHandler.randomized( 64.0f ), PITCH );

	@Override
	public void onStart() {
		APPROACHING.play( this.undeadArmy.level, AnyPos.from( this.undeadArmy.positionToAttack ).vec3() );
	}

	@Override
	public void onStateChanged() {
		if( this.undeadArmy.phase.state == Phase.State.WAVE_ONGOING ) {
			Vec3 position = new Vec3(
				this.undeadArmy.positionToAttack.getX() + this.undeadArmy.direction.x * this.undeadArmy.config.getSpawnRadius(),
				0,
				this.undeadArmy.positionToAttack.getZ() + this.undeadArmy.direction.z * this.undeadArmy.config.getSpawnRadius()
			);

			this.undeadArmy.participants.forEach( player->WAVE_STARTED.send( player, new Vec3( position.x, player.getY(), position.z ) ) );
		}
	}
}

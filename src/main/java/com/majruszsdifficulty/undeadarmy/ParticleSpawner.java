package com.majruszsdifficulty.undeadarmy;

import com.mlib.effects.ParticleHandler;
import com.mlib.math.VectorHelper;
import net.minecraft.world.phys.Vec3;

record ParticleSpawner( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		this.undeadArmy.mobsLeft.stream()
			.filter( mobInfo->mobInfo.uuid == null )
			.forEach( this::spawnParticle );
	}

	private void spawnParticle( MobInfo mobInfo ) {
		Vec3 position = VectorHelper.add( VectorHelper.vec3( mobInfo.position ), new Vec3( 0.0, mobInfo.isBoss ? 1.0 : 0.5, 0.0 ) );
		int amountOfParticles = mobInfo.isBoss ? 3 : 1;

		ParticleHandler.SOUL.spawn( this.undeadArmy.level, position, amountOfParticles );
	}
}

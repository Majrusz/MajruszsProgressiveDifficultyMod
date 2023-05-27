package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.MobInfo;
import com.mlib.effects.ParticleHandler;
import com.mlib.math.AnyPos;
import net.minecraft.world.phys.Vec3;

record ParticleSpawner( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		this.undeadArmy.mobsLeft.stream()
			.filter( mobInfo->mobInfo.uuid == null )
			.forEach( this::spawnParticle );
	}

	private void spawnParticle( MobInfo mobInfo ) {
		Vec3 position = AnyPos.from( mobInfo.position ).add( 0.0, mobInfo.isBoss ? 1.0 : 0.5, 0.0 ).vec3();
		int amountOfParticles = mobInfo.isBoss ? 3 : 1;

		ParticleHandler.SOUL.spawn( this.undeadArmy.level, position, amountOfParticles );
	}
}

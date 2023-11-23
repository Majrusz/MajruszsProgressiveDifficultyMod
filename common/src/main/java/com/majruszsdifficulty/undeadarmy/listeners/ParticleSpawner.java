package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;
import net.minecraft.core.particles.ParticleTypes;

public class ParticleSpawner {
	static {
		OnUndeadArmyTicked.listen( ParticleSpawner::update );
	}

	private static void update( OnUndeadArmyTicked data ) {
		data.undeadArmy.mobsLeft.stream()
			.filter( mobInfo->mobInfo.uuid == null )
			.forEach( mobInfo->{
				ParticleEmitter.of( ParticleTypes.SOUL )
					.position( AnyPos.from( mobInfo.position ).add( 0.0, mobInfo.isBoss ? 1.0 : 0.5, 0.0 ).vec3() )
					.count( mobInfo.isBoss ? 3 : 1 )
					.emit( data.getLevel() );
			} );
	}
}

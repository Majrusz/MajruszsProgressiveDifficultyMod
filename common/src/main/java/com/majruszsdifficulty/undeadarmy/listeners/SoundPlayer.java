package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStarted;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import net.minecraft.world.phys.Vec3;

public class SoundPlayer {
	static {
		OnUndeadArmyStarted.listen( SoundPlayer::playStart );

		OnUndeadArmyStateChanged.listen( SoundPlayer::playWaveStart )
			.addCondition( data->data.undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_ONGOING );
	}

	private static void playStart( OnUndeadArmyStarted data ) {
		SoundEmitter.of( MajruszsDifficulty.Sounds.UNDEAD_ARMY_APPROACHING )
			.position( AnyPos.from( data.undeadArmy.position ).vec3() )
			.volume( Random.nextFloat( 0.2f, 0.3f ) )
			.pitch( Random.nextFloat( 0.9f, 1.1f ) )
			.emit( data.getLevel() );
	}

	private static void playWaveStart( OnUndeadArmyStateChanged data ) {
		Vec3 position = AnyPos.from( data.undeadArmy.position )
			.add( data.undeadArmy.direction.x, 0, data.undeadArmy.direction.z )
			.mul( UndeadArmyConfig.AREA_RADIUS - 15 )
			.mul( 1, 0, 1 )
			.vec3();

		data.undeadArmy.participants.forEach( player->{
			SoundEmitter.of( MajruszsDifficulty.Sounds.UNDEAD_ARMY_WAVE_STARTED )
				.position( new Vec3( position.x, player.getY(), position.z ) )
				.volume( Random.nextFloat( 50.0f, 80.0f ) )
				.pitch( Random.nextFloat( 0.9f, 1.1f ) )
				.send( player );
		} );
	}
}

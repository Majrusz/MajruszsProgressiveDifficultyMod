package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public record Components( UndeadArmy undeadArmy, List< IComponent > components ) {
	public Components( UndeadArmy undeadArmy ) {
		this( undeadArmy, new ArrayList<>() );

		this.add( ParticipantsUpdater::new );
		this.add( BossUpdater::new );
		this.add( MobSpawner::new );
		this.add( LightningSpawner::new );
		this.add( WaveController::new );
		this.add( WeatherController::new );
		this.add( MobHighlighter::new );
		this.add( RewardsController::new );
		this.add( AdvancementsController::new );
		this.add( ProgressIndicator::new );
		this.add( MessageSender::new );
		this.add( ParticleSpawner::new );
		this.add( SoundPlayer::new );
	}

	public void dispatch( Consumer< IComponent > consumer ) {
		this.components.forEach( consumer );
	}

	private void add( Function< UndeadArmy, IComponent > provider ) {
		this.components.add( provider.apply( this.undeadArmy ) );
	}
}

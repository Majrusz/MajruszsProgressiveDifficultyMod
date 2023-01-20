package com.majruszsdifficulty.undeadarmy;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.Collection;
import java.util.List;

public class ProgressIndicator {
	final ServerBossEvent waveInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 );
	final ServerBossEvent bossInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6 );
	final Data data;
	Phase previousPhase = null;

	public ProgressIndicator( Data data ) {
		this.data = data;
	}

	public void tick( List< ServerPlayer > participants ) {
		if( this.previousPhase != this.data.phase ) {
			this.onPhaseChanged();
			this.previousPhase = this.data.phase;
		}

		this.updateVisibility();
		this.updateParticipants( participants );
		this.updateProgress();
	}

	private void onPhaseChanged() {
		this.waveInfo.setName( this.getPhaseComponent() );
		if( this.data.phase == Phase.FINISHED ) {
			this.removeParticipants();
		}
	}

	private void updateVisibility() {
		this.waveInfo.setVisible( this.data.phase != Phase.CREATED );
		this.bossInfo.setVisible( this.data.currentWave == 3 );
	}

	private void updateParticipants( List< ServerPlayer > participants ) {
		if( this.data.phase == Phase.FINISHED )
			return;

		Collection< ServerPlayer > currentParticipants = this.waveInfo.getPlayers();
		participants.forEach( player->{
			if( !currentParticipants.contains( player ) ) {
				this.waveInfo.addPlayer( player );
				this.bossInfo.addPlayer( player );
			}
		} );
		currentParticipants.forEach( player->{
			if( !participants.contains( player ) ) {
				this.waveInfo.removePlayer( player );
				this.bossInfo.removePlayer( player );
			}
		} );
	}

	private void updateProgress() {
		switch( this.data.phase ) {
			case WAVE_PREPARING -> {
				this.waveInfo.setProgress( 1.0f - this.data.getPhaseRatio() );
				this.bossInfo.setProgress( 0.0f );
			}
			case WAVE_ONGOING -> this.waveInfo.setProgress( this.data.getPhaseRatio() );
			case UNDEAD_DEFEATED -> this.waveInfo.setProgress( 1.0f );
			case UNDEAD_WON -> this.waveInfo.setProgress( 0.0f );
		}
	}

	private void removeParticipants() {
		this.waveInfo.removeAllPlayers();
		this.bossInfo.removeAllPlayers();
	}

	private Component getPhaseComponent() {
		return switch( this.data.phase ) {
			case WAVE_PREPARING -> Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", this.data.currentWave > 0 ? "between_waves" : "title" ) );
			case WAVE_ONGOING -> Component.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( Component.translatable( "majruszsdifficulty.undead_army.wave", this.data.currentWave ) );
			case UNDEAD_DEFEATED -> Component.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> Component.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> CommonComponents.EMPTY;
		};
	}
}

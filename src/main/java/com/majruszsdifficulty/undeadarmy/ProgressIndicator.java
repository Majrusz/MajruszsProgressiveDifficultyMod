package com.majruszsdifficulty.undeadarmy;

import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

class ProgressIndicator {
	final ServerBossEvent waveInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 );
	final ServerBossEvent bossInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6 );
	final UndeadArmyData data;
	Phase previousPhase = null;

	public ProgressIndicator( UndeadArmyData data ) {
		this.data = data;
	}

	public void tick( List< ServerPlayer > participants ) {
		if( this.previousPhase != this.data.getPhase() ) {
			this.onPhaseChanged( participants );
			this.previousPhase = this.data.getPhase();
		}

		this.updateVisibility();
		this.updateParticipants( participants );
		this.updateProgress();
	}

	private void onPhaseChanged( List< ServerPlayer > participants ) {
		this.waveInfo.setName( this.getPhaseComponent() );
		if( this.data.getPhase() == Phase.FINISHED ) {
			this.removeParticipants();
		} else {
			this.sendChatMessage( participants );
		}
	}

	private void updateVisibility() {
		this.waveInfo.setVisible( this.data.getPhase() != Phase.CREATED );
		this.bossInfo.setVisible( this.data.getCurrentWave() == 3 );
	}

	private void updateParticipants( List< ServerPlayer > participants ) {
		if( this.data.getPhase() == Phase.FINISHED )
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
		switch( this.data.getPhase() ) {
			case WAVE_PREPARING -> {
				this.waveInfo.setProgress( this.data.getPhaseRatio() );
				this.bossInfo.setProgress( 0.0f );
			}
			case WAVE_ONGOING -> this.waveInfo.setProgress( 1.0f - this.data.getPhaseRatio() );
			case UNDEAD_DEFEATED -> this.waveInfo.setProgress( 0.0f );
			case UNDEAD_WON -> this.waveInfo.setProgress( 1.0f );
		}
	}

	private void removeParticipants() {
		this.waveInfo.removeAllPlayers();
		this.bossInfo.removeAllPlayers();
	}

	private Component getPhaseComponent() {
		return switch( this.data.getPhase() ) {
			case WAVE_PREPARING -> Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", this.data.getCurrentWave() > 0 ? "between_waves" : "title" ) );
			case WAVE_ONGOING -> Component.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( Component.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( this.data.getCurrentWave() ) ) );
			case UNDEAD_DEFEATED -> Component.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> Component.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> CommonComponents.EMPTY;
		};
	}

	private void sendChatMessage( List< ServerPlayer > participants ) {
		this.getChatMessageId()
			.ifPresent( message->participants.forEach( participant->participant.displayClientMessage( message, false ) ) );
	}

	private Optional< MutableComponent > getChatMessageId() {
		if( this.data.getPhase() == Phase.WAVE_PREPARING && this.data.getCurrentWave() == 0 ) {
			String directionId = this.data.getDirection().toString().toLowerCase();
			MutableComponent direction = Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", directionId ) );
			MutableComponent approaching = Component.translatable( "majruszsdifficulty.undead_army.approaching", direction );

			return Optional.of( approaching.withStyle( ChatFormatting.DARK_PURPLE ) );
		} else if( this.data.getPhase() == Phase.WAVE_ONGOING && this.data.getCurrentWave() == 1 ) {
			MutableComponent approached = Component.translatable( "majruszsdifficulty.undead_army.approached" );

			return Optional.of( approached.withStyle( ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE ) );
		} else {
			return Optional.empty();
		}
	}
}

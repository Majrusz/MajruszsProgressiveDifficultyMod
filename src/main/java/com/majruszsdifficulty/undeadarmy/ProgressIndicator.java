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

class ProgressIndicator implements IComponent {
	final ServerBossEvent waveInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 );
	final ServerBossEvent bossInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6 );
	final UndeadArmy undeadArmy;

	public ProgressIndicator( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		this.updateVisibility();
		this.updateParticipants();
		this.updateProgress();
	}

	@Override
	public void onPhaseChanged() {
		this.waveInfo.setName( this.getPhaseComponent() );
		if( this.undeadArmy.phase == Phase.FINISHED ) {
			this.removeParticipants();
		} else {
			this.sendChatMessage();
		}
	}

	private void updateVisibility() {
		this.waveInfo.setVisible( this.undeadArmy.phase != Phase.CREATED );
		this.bossInfo.setVisible( false );
	}

	private void updateParticipants() {
		if( this.undeadArmy.phase == Phase.FINISHED )
			return;

		Collection< ServerPlayer > currentParticipants = this.waveInfo.getPlayers();
		this.undeadArmy.participants.forEach( player->{
			if( !currentParticipants.contains( player ) ) {
				this.waveInfo.addPlayer( player );
				this.bossInfo.addPlayer( player );
			}
		} );
		currentParticipants.forEach( player->{
			if( !this.undeadArmy.participants.contains( player ) ) {
				this.waveInfo.removePlayer( player );
				this.bossInfo.removePlayer( player );
			}
		} );
	}

	private void updateProgress() {
		switch( this.undeadArmy.phase ) {
			case WAVE_PREPARING -> {
				this.waveInfo.setProgress( this.undeadArmy.getPhaseRatio() );
				this.bossInfo.setProgress( 0.0f );
			}
			case WAVE_ONGOING -> this.waveInfo.setProgress( 1.0f - this.undeadArmy.getPhaseRatio() );
			case UNDEAD_DEFEATED -> this.waveInfo.setProgress( 0.0f );
			case UNDEAD_WON -> this.waveInfo.setProgress( 1.0f );
		}
	}

	private void removeParticipants() {
		this.waveInfo.removeAllPlayers();
		this.bossInfo.removeAllPlayers();
	}

	private Component getPhaseComponent() {
		return switch( this.undeadArmy.phase ) {
			case WAVE_PREPARING -> Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", this.undeadArmy.currentWave > 0 ? "between_waves" : "title" ) );
			case WAVE_ONGOING -> Component.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( Component.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( this.undeadArmy.currentWave ) ) );
			case UNDEAD_DEFEATED -> Component.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> Component.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> CommonComponents.EMPTY;
		};
	}

	private void sendChatMessage() {
		this.getChatMessageId()
			.ifPresent( message->this.undeadArmy.participants.forEach( participant->participant.displayClientMessage( message, false ) ) );
	}

	private Optional< MutableComponent > getChatMessageId() {
		if( this.undeadArmy.phase == Phase.WAVE_PREPARING && this.undeadArmy.currentWave == 0 ) {
			String directionId = this.undeadArmy.direction.toString().toLowerCase();
			MutableComponent direction = Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", directionId ) );
			MutableComponent approaching = Component.translatable( "majruszsdifficulty.undead_army.approaching", direction );

			return Optional.of( approaching.withStyle( ChatFormatting.DARK_PURPLE ) );
		} else if( this.undeadArmy.phase == Phase.WAVE_ONGOING && this.undeadArmy.currentWave == 1 ) {
			MutableComponent approached = Component.translatable( "majruszsdifficulty.undead_army.approached" );

			return Optional.of( approached.withStyle( ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE ) );
		} else {
			return Optional.empty();
		}
	}

}

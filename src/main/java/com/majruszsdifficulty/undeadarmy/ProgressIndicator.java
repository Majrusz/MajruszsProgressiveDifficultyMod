package com.majruszsdifficulty.undeadarmy;

import com.mlib.text.TextHelper;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.Collection;

class ProgressIndicator implements IComponent {
	final ServerBossEvent waveInfo = ( ServerBossEvent )new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 ).setCreateWorldFog( true );
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
		if( this.undeadArmy.phase.state == Phase.State.FINISHED ) {
			this.removeParticipants();
		}
	}

	@Override
	public void onGameReload() {
		this.waveInfo.setName( this.getPhaseComponent() );
	}

	private void updateVisibility() {
		this.waveInfo.setVisible( this.undeadArmy.phase.state != Phase.State.CREATED );
		this.bossInfo.setVisible( false );
	}

	private void updateParticipants() {
		if( this.undeadArmy.phase.state == Phase.State.FINISHED )
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
		switch( this.undeadArmy.phase.state ) {
			case WAVE_PREPARING -> {
				this.waveInfo.setProgress( this.undeadArmy.getPhaseRatio() );
				this.bossInfo.setProgress( 0.0f );
			}
			case WAVE_ONGOING -> this.waveInfo.setProgress( this.getHealthRatioLeft() );
			case UNDEAD_DEFEATED -> this.waveInfo.setProgress( 0.0f );
			case UNDEAD_WON -> this.waveInfo.setProgress( 1.0f );
		}
	}

	private void removeParticipants() {
		this.waveInfo.removeAllPlayers();
		this.bossInfo.removeAllPlayers();
	}

	private Component getPhaseComponent() {
		return switch( this.undeadArmy.phase.state ) {
			case WAVE_PREPARING -> Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", this.undeadArmy.currentWave > 0 ? "between_waves" : "title" ) );
			case WAVE_ONGOING -> Component.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( Component.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( this.undeadArmy.currentWave ) ) );
			case UNDEAD_DEFEATED -> Component.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> Component.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> CommonComponents.EMPTY;
		};
	}

	private float getHealthRatioLeft() {
		boolean hasNotAnyMobSpawned = this.undeadArmy.mobsLeft.stream().allMatch( mob->mob.toEntity( this.undeadArmy.level ) == null );
		if( hasNotAnyMobSpawned )
			return 1.0f;

		float healthLeft = 0.0f;
		float healthTotal = Math.max( this.undeadArmy.phase.healthTotal, 1.0f );
		for( MobInfo mobInfo : this.undeadArmy.mobsLeft ) {
			healthLeft += mobInfo.getHealth( this.undeadArmy.level );
		}

		return Mth.clamp( healthLeft / healthTotal, 0.0f, 1.0f );
	}
}

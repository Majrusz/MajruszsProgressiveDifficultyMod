package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyLoaded;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;

public class ProgressIndicator {
	static {
		OnUndeadArmyTicked.listen( ProgressIndicator::update );

		OnUndeadArmyStateChanged.listen( ProgressIndicator::update );

		OnUndeadArmyLoaded.listen( ProgressIndicator::update );
	}

	private static void update( OnUndeadArmyTicked data ) {
		ProgressIndicator.updateVisibility( data.undeadArmy );
		ProgressIndicator.updateParticipants( data.undeadArmy );
		ProgressIndicator.updateProgress( data.undeadArmy );
	}

	private static void update( OnUndeadArmyStateChanged data ) {
		data.undeadArmy.waveInfo.setName( ProgressIndicator.getPhaseComponent( data.undeadArmy ) );
		if( data.undeadArmy.phase.state == UndeadArmy.Phase.State.FINISHED ) {
			ProgressIndicator.removeParticipants( data.undeadArmy );
		}
	}

	private static void update( OnUndeadArmyLoaded data ) {
		data.undeadArmy.waveInfo.setName( ProgressIndicator.getPhaseComponent( data.undeadArmy ) );
	}

	private static void updateVisibility( UndeadArmy undeadArmy ) {
		boolean isBossAlive = undeadArmy.boss != null;

		undeadArmy.waveInfo.setVisible( undeadArmy.phase.state != UndeadArmy.Phase.State.STARTED );
		if( !undeadArmy.bossInfo.isVisible() && isBossAlive ) {
			undeadArmy.bossInfo.setName( ProgressIndicator.getBossName( undeadArmy ) );
		}
		undeadArmy.bossInfo.setVisible( isBossAlive );
	}

	private static void updateParticipants( UndeadArmy undeadArmy ) {
		if( undeadArmy.phase.state == UndeadArmy.Phase.State.FINISHED ) {
			return;
		}

		Collection< ServerPlayer > currentParticipants = new ArrayList<>( undeadArmy.waveInfo.getPlayers() );
		undeadArmy.participants.forEach( player->{
			if( !currentParticipants.contains( player ) ) {
				undeadArmy.waveInfo.addPlayer( player );
				undeadArmy.bossInfo.addPlayer( player );
			}
		} );
		currentParticipants.forEach( player->{
			if( !undeadArmy.participants.contains( player ) ) {
				undeadArmy.waveInfo.removePlayer( player );
				undeadArmy.bossInfo.removePlayer( player );
			}
		} );
	}

	private static void updateProgress( UndeadArmy undeadArmy ) {
		switch( undeadArmy.phase.state ) {
			case STARTED -> undeadArmy.waveInfo.setProgress( 0.0f );
			case WAVE_PREPARING -> {
				undeadArmy.waveInfo.setProgress( undeadArmy.phase.getRatio() );
				undeadArmy.bossInfo.setProgress( 0.0f );
			}
			case WAVE_ONGOING -> {
				undeadArmy.waveInfo.setProgress( ProgressIndicator.getHealthRatioLeft( undeadArmy ) );
				undeadArmy.bossInfo.setProgress( ProgressIndicator.getBossHealthRatioLeft( undeadArmy ) );
			}
			case UNDEAD_DEFEATED -> undeadArmy.waveInfo.setProgress( 0.0f );
			case UNDEAD_WON -> undeadArmy.waveInfo.setProgress( 1.0f );
		}
	}

	private static void removeParticipants( UndeadArmy undeadArmy ) {
		undeadArmy.waveInfo.removeAllPlayers();
		undeadArmy.bossInfo.removeAllPlayers();
	}

	private static Component getPhaseComponent( UndeadArmy undeadArmy ) {
		return switch( undeadArmy.phase.state ) {
			case WAVE_PREPARING -> TextHelper.translatable( "majruszsdifficulty.undead_army.%s".formatted( undeadArmy.currentWave > 0 ? "between_waves" : "title" ) );
			case WAVE_ONGOING -> TextHelper.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( TextHelper.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( undeadArmy.currentWave ) ) );
			case UNDEAD_DEFEATED -> TextHelper.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> TextHelper.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> TextHelper.empty();
		};
	}

	private static float getHealthRatioLeft( UndeadArmy undeadArmy ) {
		if( ProgressIndicator.hasNooneSpawnedYet( undeadArmy ) ) {
			return 1.0f;
		}

		float healthLeft = 0.0f;
		float healthTotal = Math.max( undeadArmy.phase.healthTotal, 1.0f );
		for( UndeadArmy.MobInfo mobInfo : undeadArmy.mobsLeft ) {
			healthLeft += mobInfo.getHealth( undeadArmy.getLevel() );
		}

		return Mth.clamp( healthLeft / healthTotal, 0.0f, 1.0f );
	}

	private static boolean hasNooneSpawnedYet( UndeadArmy undeadArmy ) {
		return undeadArmy.mobsLeft.stream().allMatch( mob->mob.toEntity( undeadArmy.getLevel() ) == null );
	}

	private static float getBossHealthRatioLeft( UndeadArmy undeadArmy ) {
		return undeadArmy.boss instanceof LivingEntity boss ? Mth.clamp( boss.getHealth() / boss.getMaxHealth(), 0.0f, 1.0f ) : 0.0f;
	}

	private static Component getBossName( UndeadArmy undeadArmy ) {
		return undeadArmy.boss.getDisplayName().copy().withStyle( ChatFormatting.RED );
	}
}

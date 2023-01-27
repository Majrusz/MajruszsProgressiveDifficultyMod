package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
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

class WaveController implements IComponent {
	final UndeadArmy undeadArmy;

	public WaveController( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		switch( this.undeadArmy.phase ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.undeadArmy.phaseTicksLeft = Math.max( this.undeadArmy.phaseTicksLeft - 1, 0 );
	}

	@Override
	public void onPhaseChanged() {

	}

	private void tickCreated() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
		// this.undeadArmy.generateWaveMobs( this.config, this.level );
	}

	private void tickWavePreparing() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.setPhase( Phase.WAVE_ONGOING, Utility.secondsToTicks( 5.0 ) );
		++this.undeadArmy.currentWave;
	}

	private void tickWaveOngoing() {
		/*if( this.undeadArmy.phaseTicksLeft % 10 == 0 && this.undeadArmy.tryToSpawnNextMob( this.level ) ) {
			;
			// MajruszLibrary.log( "%s %s", mob.type.get().toString(), mob.isBoss.get().toString() );
		}*/

		if( this.undeadArmy.isPhaseOver() ) {
			// this.data.setPhase( Phase.UNDEAD_WON, Utility.secondsToTicks( 2.0 ) );
			if( this.undeadArmy.isLastWave() ) {
				this.undeadArmy.setPhase( Phase.UNDEAD_DEFEATED, Utility.secondsToTicks( 2.0 ) );
			} else {
				this.undeadArmy.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
				// this.undeadArmy.generateWaveMobs( this.config, this.level );
			}
		}
	}

	private void tickUndeadDefeated() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.finish();
	}

	private void tickUndeadWon() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.finish();
	}
}

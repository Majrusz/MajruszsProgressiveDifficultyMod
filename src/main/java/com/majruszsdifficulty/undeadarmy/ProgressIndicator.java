package com.majruszsdifficulty.undeadarmy;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.List;

public class ProgressIndicator {
	final ServerBossEvent waveInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 );
	final ServerBossEvent bossInfo = new ServerBossEvent( CommonComponents.EMPTY, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6 );

	public ProgressIndicator() {}

	public void updateInfo( Data data ) {
		this.waveInfo.setName( ComponentBuilder.buildWaveComponent( data ) );
	}

	public void updateParticipants( List< ServerPlayer > participants ) {

	}
}

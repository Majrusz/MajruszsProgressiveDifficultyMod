package com.majruszsdifficulty.gamestage.contexts;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.gamestage.GameStage;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class OnPlayerGameStageChanged {
	public final GameStage previous;
	public final GameStage current;
	public final Player player;

	public static Event< OnPlayerGameStageChanged > listen( Consumer< OnPlayerGameStageChanged > consumer ) {
		return Events.get( OnPlayerGameStageChanged.class ).add( consumer );
	}

	public OnPlayerGameStageChanged( GameStage previous, GameStage current, Player player ) {
		this.previous = previous;
		this.current = current;
		this.player = player;
	}
}

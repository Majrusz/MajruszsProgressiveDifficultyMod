package com.majruszsdifficulty.gamestage.contexts;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.gamestage.GameStage;

import java.util.function.Consumer;

public class OnGlobalGameStageChanged {
	public final GameStage previous;
	public final GameStage current;

	public static Event< OnGlobalGameStageChanged > listen( Consumer< OnGlobalGameStageChanged > consumer ) {
		return Events.get( OnGlobalGameStageChanged.class ).add( consumer );
	}

	public OnGlobalGameStageChanged( GameStage previous, GameStage current ) {
		this.previous = previous;
		this.current = current;
	}
}

package com.majruszsdifficulty.gamestage.contexts;

import com.majruszlibrary.contexts.base.Context;
import com.majruszlibrary.contexts.base.Contexts;
import com.majruszsdifficulty.gamestage.GameStage;

import java.util.function.Consumer;

public class OnGlobalGameStageChanged {
	public final GameStage previous;
	public final GameStage current;

	public static Context< OnGlobalGameStageChanged > listen( Consumer< OnGlobalGameStageChanged > consumer ) {
		return Contexts.get( OnGlobalGameStageChanged.class ).add( consumer );
	}

	public OnGlobalGameStageChanged( GameStage previous, GameStage current ) {
		this.previous = previous;
		this.current = current;
	}
}

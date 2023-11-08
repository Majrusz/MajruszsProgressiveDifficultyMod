package com.majruszsdifficulty.contexts;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.Contexts;

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

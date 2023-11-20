package com.majruszsdifficulty.contexts;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszlibrary.contexts.base.Context;
import com.majruszlibrary.contexts.base.Contexts;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class OnPlayerGameStageChanged {
	public final GameStage previous;
	public final GameStage current;
	public final Player player;

	public static Context< OnPlayerGameStageChanged > listen( Consumer< OnPlayerGameStageChanged > consumer ) {
		return Contexts.get( OnPlayerGameStageChanged.class ).add( consumer );
	}

	public OnPlayerGameStageChanged( GameStage previous, GameStage current, Player player ) {
		this.previous = previous;
		this.current = current;
		this.player = player;
	}
}

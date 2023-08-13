package com.majruszsdifficulty.contexts;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.Contexts;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnGameStageChange {
	public static Context< Data > listen( Consumer< Data > consumer ) {
		return Contexts.get( Data.class ).add( consumer );
	}

	public static Data dispatch( @Nullable MinecraftServer server, GameStage previous, GameStage current ) {
		return Contexts.get( Data.class ).dispatch( new Data( server, previous, current ) );
	}

	public static class Data {
		@Nullable
		public final MinecraftServer server;
		public final GameStage previous;
		public final GameStage current;

		public Data( @Nullable MinecraftServer server, GameStage previous, GameStage current ) {
			this.server = server;
			this.previous = previous;
			this.current = current;
		}

		public boolean isLoadedFromDisk() {
			return this.server == null;
		}
	}
}

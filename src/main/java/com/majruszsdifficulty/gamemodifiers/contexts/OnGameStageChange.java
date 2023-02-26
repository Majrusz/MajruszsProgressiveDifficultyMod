package com.majruszsdifficulty.gamemodifiers.contexts;

import com.majruszsdifficulty.GameStage;
import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.Contexts;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnGameStageChange {
	public static void accept( Data data ) {
		Context.CONTEXTS.accept( data );
	}

	public static class Context extends ContextBase< Data > {
		static final Contexts< Data, Context > CONTEXTS = new Contexts<>();

		public Context( Consumer< Data > consumer ) {
			super( consumer );

			CONTEXTS.add( this );
		}
	}

	public static class Data extends ContextData {
		@Nullable
		public final MinecraftServer server;
		public final GameStage previous;
		public final GameStage current;

		public Data( @Nullable MinecraftServer server, GameStage previous, GameStage current ) {
			super( server != null ? server.overworld() : null );

			this.server = server;
			this.previous = previous;
			this.current = current;
		}

		public boolean isLoadedFromDisk() {
			return this.server == null;
		}
	}
}

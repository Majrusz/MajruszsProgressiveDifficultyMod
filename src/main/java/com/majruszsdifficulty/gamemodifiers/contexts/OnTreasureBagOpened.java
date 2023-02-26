package com.majruszsdifficulty.gamemodifiers.contexts;

import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.Contexts;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class OnTreasureBagOpened {
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
		public final Player player;
		public final TreasureBagItem treasureBag;
		public final List< ItemStack > generatedLoot;

		public Data( Player player, TreasureBagItem treasureBag, List< ItemStack > generatedLoot ) {
			super( player );

			this.player = player;
			this.treasureBag = treasureBag;
			this.generatedLoot = generatedLoot;
		}
	}
}

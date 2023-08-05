package com.majruszsdifficulty.gamemodifiers.contexts;

import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.Contexts;
import com.mlib.contexts.data.ILevelData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class OnTreasureBagOpened {
	public static Context< Data > listen( Consumer< Data > consumer ) {
		return Contexts.get( Data.class ).add( consumer );
	}

	public static Data dispatch( Player player, TreasureBagItem treasureBag, List< ItemStack > generatedLoot ) {
		return Contexts.get( Data.class ).dispatch( new Data( player, treasureBag, generatedLoot ) );
	}

	public static class Data implements ILevelData {
		public final Player player;
		public final TreasureBagItem treasureBag;
		public final List< ItemStack > generatedLoot;

		public Data( Player player, TreasureBagItem treasureBag, List< ItemStack > generatedLoot ) {
			this.player = player;
			this.treasureBag = treasureBag;
			this.generatedLoot = generatedLoot;
		}

		@Override
		public Level getLevel() {
			return this.player.level();
		}
	}
}

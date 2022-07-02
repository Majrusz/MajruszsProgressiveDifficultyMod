package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.treasurebags.LootProgressManager;
import com.mlib.Utility;
import com.mlib.commands.EntityCommand;
import com.mlib.commands.IRegistrableCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/** Command that resets progress of dropped items. */
public class TreasureBagResetProgressCommands extends EntityCommand implements IRegistrableCommand {
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Entity entity ) {
		Player player = Utility.castIfPossible( Player.class, entity );
		if( player != null ) {
			LootProgressManager.cleanProgress( player );
			source.sendSuccess( Component.translatable( "commands.treasurebag.reset", entity.getName() ), true );
			return 0;
		}

		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.TREASURE_BAG_ARGUMENT, literal( "reset" ) );
		registerEntityCommand( commandDispatcher, commandData );
	}
}

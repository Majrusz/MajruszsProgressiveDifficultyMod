package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.features.treasure_bag.LootProgress;
import com.mlib.CommonHelper;
import com.mlib.commands.EntityCommand;
import com.mlib.commands.IRegistrableCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/** Command that resets progress of dropped items. */
public class TreasureBagResetProgressCommands extends EntityCommand implements IRegistrableCommand {
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Entity entity ) {
		Player player = CommonHelper.castIfPossible( Player.class, entity );
		if( player != null ) {
			LootProgress.cleanProgress( player );
			source.sendSuccess( new TranslatableComponent( "commands.treasurebag.reset", entity.getName() ), true );
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

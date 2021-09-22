package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.features.undead_army.UndeadArmyManager;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.commands.LocationCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Command that starts the Undead Army at given position. */
public class StartUndeadArmyCommands extends LocationCommand implements IRegistrableCommand {
	/** Starts the Undead Army at given position and sends information to the caller. */
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Vec3 position ) {
		UndeadArmyManager undeadArmyManager = RegistryHandler.UNDEAD_ARMY_MANAGER;
		BlockPos blockPosition = new BlockPos( position );
		if( undeadArmyManager.findNearestUndeadArmy( blockPosition ) == null && undeadArmyManager.tryToSpawn( blockPosition ) ) {
			source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.started", position ), true );
			return 0;
		}

		source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.cannotstart", position ), true );
		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "start" ) );
		registerLocationCommand( commandDispatcher, commandData );
	}
}

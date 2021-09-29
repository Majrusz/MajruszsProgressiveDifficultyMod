package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.features.undead_army.UndeadArmy;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.commands.PositionCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Command that sends information about the Undead Army progress at given position. */
public class UndeadArmyUndeadLeftCommands extends PositionCommand implements IRegistrableCommand {
	/** Sends information about the Undead Army progress at given position and sends information to the caller. */
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Vec3 position ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			int unitsLeft = undeadArmy.countUndeadEntitiesLeft();
			source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.progress", position, unitsLeft ), true );
			return 0;
		}

		source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.missing", position ), true );
		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "undeadleft" ) );
		registerLocationCommand( commandDispatcher, commandData );
	}
}

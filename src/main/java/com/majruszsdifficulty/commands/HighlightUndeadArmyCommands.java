package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.RegistryHandler;
import com.majruszsdifficulty.features.undead_army.UndeadArmy;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.commands.PositionCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Command that highlights all mobs from the Undead Army at given position. */
public class HighlightUndeadArmyCommands extends PositionCommand implements IRegistrableCommand {
	/** Highlights all mobs from the Undead Army at given position and sends information to the caller. */
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Vec3 position ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			undeadArmy.highlightUndeadArmy();
			source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.highlighted", position ), true );
			return 0;
		}

		source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.missing", position ), true );
		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "highlight" ) );
		registerLocationCommand( commandDispatcher, commandData );
	}
}

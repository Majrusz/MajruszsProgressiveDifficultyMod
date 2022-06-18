package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.RegistryHandler;
import com.majruszsdifficulty.features.undead_army.Direction;
import com.majruszsdifficulty.features.undead_army.UndeadArmyManager;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.commands.PositionCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/** Command that starts the Undead Army at given position. */
public class StartUndeadArmyCommands extends PositionCommand implements IRegistrableCommand {
	private static final String ENUM_ARGUMENT_NAME = "direction";
	private static final RequiredArgumentBuilder< CommandSourceStack, ? > ENUM_ARGUMENT = enumArgument( ENUM_ARGUMENT_NAME, Direction.class );

	/** Starts the Undead Army at given position and sends information to the caller. */
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Vec3 position ) {
		UndeadArmyManager undeadArmyManager = RegistryHandler.UNDEAD_ARMY_MANAGER;
		BlockPos blockPosition = new BlockPos( position );
		Optional< Direction > direction = getOptionalEnum( context, ENUM_ARGUMENT_NAME, Direction.class );
		if( undeadArmyManager.findNearestUndeadArmy( blockPosition ) == null && undeadArmyManager.tryToSpawn( blockPosition, direction ) ) {
			source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.started", position ), true );
			return 0;
		}

		source.sendSuccess( CommandsHelper.createBaseMessageWithPosition( "commands.undeadarmy.cannotstart", position ), true );
		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data enumCommandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "start" ), ENUM_ARGUMENT );
		registerLocationCommand( commandDispatcher, enumCommandData );

		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "start" ) );
		registerLocationCommand( commandDispatcher, commandData );
	}
}

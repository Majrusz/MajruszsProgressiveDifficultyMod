package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.events.UndeadArmy;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;

/** Command responsible for stopping undead army in the player position. */
public final class StopUndeadArmyCommand {
	private StopUndeadArmyCommand() {}

	public static void register( CommandDispatcher< CommandSource > dispatcher ) {
		dispatcher.register( Commands.literal( "undead_army" )
			.requires( source->source.hasPermissionLevel( 4 ) )
			.then( Commands.literal( "stop" )
				.executes( entity->stopUndeadArmy( entity.getSource() ) ) ) );
	}

	public static int stopUndeadArmy( CommandSource source ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( source.getPos() ) );

		if( undeadArmy == null )
			return -1;

		undeadArmy.finish();
		return 0;
	}
}

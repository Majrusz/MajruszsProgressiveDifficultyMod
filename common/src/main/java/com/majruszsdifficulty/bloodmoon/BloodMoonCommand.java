package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.command.Command;
import com.majruszlibrary.command.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class BloodMoonCommand {
	static {
		Command.create()
			.literal( "bloodmoon" )
			.hasPermission( 4 )
			.literal( "start" )
			.execute( BloodMoonCommand::start )
			.register();

		Command.create()
			.literal( "bloodmoon" )
			.hasPermission( 4 )
			.literal( "stop" )
			.execute( BloodMoonCommand::stop )
			.register();
	}

	private static int start( CommandData data ) throws CommandSyntaxException {
		BloodMoonHelper.start();

		return 0;
	}

	private static int stop( CommandData data ) throws CommandSyntaxException {
		BloodMoonHelper.stop();

		return 0;
	}
}

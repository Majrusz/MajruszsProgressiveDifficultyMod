package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.annotations.AutoInstance;

@AutoInstance
public class UndeadArmyStopCommand extends UndeadArmyCommand {
	public UndeadArmyStopCommand() {
		super( "stop", "finished", UndeadArmy::finish );
	}
}

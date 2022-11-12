package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.annotations.AutoInstance;

@AutoInstance
public class UndeadArmyKillAllCommand extends UndeadArmyCommand {
	public UndeadArmyKillAllCommand() {
		super( "killall", "killed", UndeadArmy::killAllUndeadArmyMobs );
	}
}

package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.modhelper.AutoInstance;

@AutoInstance
public class UndeadArmyKillAllCommand extends UndeadArmyCommand {
	public UndeadArmyKillAllCommand() {
		super( "killall", "killed", UndeadArmy::killAllUndeadArmyMobs );
	}
}

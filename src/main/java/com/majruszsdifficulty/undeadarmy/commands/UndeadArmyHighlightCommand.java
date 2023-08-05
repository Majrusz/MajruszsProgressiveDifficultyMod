package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.modhelper.AutoInstance;

@AutoInstance
public class UndeadArmyHighlightCommand extends UndeadArmyCommand {
	public UndeadArmyHighlightCommand() {
		super( "highlight", "highlighted", UndeadArmy::highlightArmy );
	}
}

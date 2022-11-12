package com.majruszsdifficulty.commands;

import com.mlib.annotations.AutoInstance;
import net.minecraft.network.chat.Component;

@AutoInstance
public class UndeadArmyMobsLeftCommand extends UndeadArmyCommand {
	public UndeadArmyMobsLeftCommand() {
		super( "undeadleft", ( undeadArmy, position )->{
			int mobsLeft = undeadArmy.countMobsLeft();
			return Component.translatable( "commands.undeadarmy.undeadleft", asVec3i( position ), mobsLeft );
		} );
	}
}

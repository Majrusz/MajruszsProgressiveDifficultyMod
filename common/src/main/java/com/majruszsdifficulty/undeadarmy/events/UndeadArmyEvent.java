package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.type.ILevelEvent;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import net.minecraft.world.level.Level;

public class UndeadArmyEvent implements ILevelEvent {
	public final UndeadArmy undeadArmy;

	public UndeadArmyEvent( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public Level getLevel() {
		return this.undeadArmy.getLevel();
	}
}

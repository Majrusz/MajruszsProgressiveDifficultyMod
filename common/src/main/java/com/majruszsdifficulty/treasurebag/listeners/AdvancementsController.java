package com.majruszsdifficulty.treasurebag.listeners;

import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.registry.Registries;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementsController {
	static {
		OnTreasureBagOpened.listen( AdvancementsController::trigger )
			.addCondition( Condition.isLogicalServer() );
	}

	private static void trigger( OnTreasureBagOpened data ) {
		MajruszsDifficulty.HELPER.triggerAchievement( ( ServerPlayer )data.player, Registries.ITEMS.getId( data.treasureBag ).toString() );
	}
}

package com.majruszsdifficulty.treasurebag.listeners;

import com.majruszlibrary.events.OnRaidDefeated;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.ItemHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyDefeated;
import net.minecraft.world.item.ItemStack;

public class RaidRewarder {
	static {
		OnRaidDefeated.listen( RaidRewarder::giveTreasureBags )
			.addCondition( Condition.isLogicalServer() );

		OnUndeadArmyDefeated.listen( RaidRewarder::giveTreasureBags )
			.addCondition( Condition.isLogicalServer() );
	}

	private static void giveTreasureBags( OnRaidDefeated data ) {
		data.players.forEach( player->ItemHelper.giveToPlayer( new ItemStack( MajruszsDifficulty.Items.PILLAGER_TREASURE_BAG.get() ), player ) );
	}

	private static void giveTreasureBags( OnUndeadArmyDefeated data ) {
		data.undeadArmy.participants.forEach( player->ItemHelper.giveToPlayer( new ItemStack( MajruszsDifficulty.Items.UNDEAD_ARMY_TREASURE_BAG.get() ), player ) );
	}
}

package com.majruszsdifficulty.events.bloodmoon.listeners;

import com.majruszsdifficulty.events.bloodmoon.contexts.OnBloodMoonFinished;
import com.majruszsdifficulty.events.bloodmoon.contexts.OnBloodMoonStarted;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class Notifier {
	static {
		OnBloodMoonStarted.listen( data->Notifier.sendStartMessage( "majruszsdifficulty.blood_moon.started" ) )
			.addCondition( Condition.isLogicalServer() );

		OnBloodMoonFinished.listen( data->Notifier.sendStartMessage( "majruszsdifficulty.blood_moon.finished" ) )
			.addCondition( Condition.isLogicalServer() );
	}

	private static void sendStartMessage( String id ) {
		Component message = TextHelper.translatable( id ).withStyle( ChatFormatting.RED );

		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->player.sendSystemMessage( message ) );
	}
}

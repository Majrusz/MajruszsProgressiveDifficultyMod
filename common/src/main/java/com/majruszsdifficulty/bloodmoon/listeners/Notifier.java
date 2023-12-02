package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonFinished;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonStarted;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class Notifier {
	static {
		OnBloodMoonStarted.listen( data->Notifier.sendStartMessage( "majruszsdifficulty.blood_moon.started" ) )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->Side.getServer() != null );

		OnBloodMoonFinished.listen( data->Notifier.sendStartMessage( "majruszsdifficulty.blood_moon.finished" ) )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->Side.getServer() != null );
	}

	private static void sendStartMessage( String id ) {
		Component message = TextHelper.translatable( id ).withStyle( ChatFormatting.RED );

		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->player.sendSystemMessage( message ) );
	}
}

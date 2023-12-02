package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public class MessageSender {
	static {
		OnUndeadArmyStateChanged.listen( MessageSender::send )
			.addCondition( data->data.undeadArmy.phase.state != UndeadArmy.Phase.State.FINISHED );
	}

	private static void send( OnUndeadArmyStateChanged data ) {
		MessageSender.getChatMessage( data.undeadArmy )
			.ifPresent( message->data.undeadArmy.participants.forEach( participant->participant.displayClientMessage( message, false ) ) );
	}

	private static Optional< MutableComponent > getChatMessage( UndeadArmy undeadArmy ) {
		if( undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_PREPARING && undeadArmy.currentWave == 0 ) {
			String directionId = undeadArmy.direction.toString().toLowerCase();
			MutableComponent direction = TextHelper.translatable( "majruszsdifficulty.undead_army.%s".formatted( directionId ) );
			MutableComponent approaching = TextHelper.translatable( "majruszsdifficulty.undead_army.approaching", direction );

			return Optional.of( approaching.withStyle( ChatFormatting.DARK_PURPLE ) );
		} else if( undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_ONGOING && undeadArmy.currentWave == 1 ) {
			MutableComponent approached = TextHelper.translatable( "majruszsdifficulty.undead_army.approached" );

			return Optional.of( approached.withStyle( ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE ) );
		} else {
			return Optional.empty();
		}
	}
}

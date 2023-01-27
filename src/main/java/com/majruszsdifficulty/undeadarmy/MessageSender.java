package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

class MessageSender implements IComponent {
	final UndeadArmy undeadArmy;

	public MessageSender( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void onPhaseChanged() {
		if( this.undeadArmy.phase != Phase.FINISHED ) {
			this.sendChatMessage();
		}
	}

	private void sendChatMessage() {
		this.getChatMessageId()
			.ifPresent( message->this.undeadArmy.participants.forEach( participant->participant.displayClientMessage( message, false ) ) );
	}

	private Optional< MutableComponent > getChatMessageId() {
		if( this.undeadArmy.phase == Phase.WAVE_PREPARING && this.undeadArmy.currentWave == 0 ) {
			String directionId = this.undeadArmy.direction.toString().toLowerCase();
			MutableComponent direction = Component.translatable( String.format( "majruszsdifficulty.undead_army.%s", directionId ) );
			MutableComponent approaching = Component.translatable( "majruszsdifficulty.undead_army.approaching", direction );

			return Optional.of( approaching.withStyle( ChatFormatting.DARK_PURPLE ) );
		} else if( this.undeadArmy.phase == Phase.WAVE_ONGOING && this.undeadArmy.currentWave == 1 ) {
			MutableComponent approached = Component.translatable( "majruszsdifficulty.undead_army.approached" );

			return Optional.of( approached.withStyle( ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE ) );
		} else {
			return Optional.empty();
		}
	}

}

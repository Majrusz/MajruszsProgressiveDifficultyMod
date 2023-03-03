package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Optional;

record MessageSender( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void onStateChanged() {
		if( this.undeadArmy.phase.state != Phase.State.FINISHED ) {
			this.sendChatMessage();
		}
	}

	private void sendChatMessage() {
		this.getChatMessageId()
			.ifPresent( message->this.undeadArmy.participants.forEach( participant->participant.displayClientMessage( message, false ) ) );
	}

	private Optional< MutableComponent > getChatMessageId() {
		if( this.undeadArmy.phase.state == Phase.State.WAVE_PREPARING && this.undeadArmy.currentWave == 0 ) {
			String directionId = this.undeadArmy.direction.toString().toLowerCase();
			MutableComponent direction = new TranslatableComponent( String.format( "majruszsdifficulty.undead_army.%s", directionId ) );
			MutableComponent approaching = new TranslatableComponent( "majruszsdifficulty.undead_army.approaching", direction );

			return Optional.of( approaching.withStyle( ChatFormatting.DARK_PURPLE ) );
		} else if( this.undeadArmy.phase.state == Phase.State.WAVE_ONGOING && this.undeadArmy.currentWave == 1 ) {
			MutableComponent approached = new TranslatableComponent( "majruszsdifficulty.undead_army.approached" );

			return Optional.of( approached.withStyle( ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE ) );
		} else {
			return Optional.empty();
		}
	}

}

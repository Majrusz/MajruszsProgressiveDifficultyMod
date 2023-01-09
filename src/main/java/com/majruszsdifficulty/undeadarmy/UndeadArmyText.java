package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class UndeadArmyText {
	static final String TITLE_ID = "majruszsdifficulty.undead_army.title";
	static final String WAVE_ID = "majruszsdifficulty.undead_army.wave";
	static final String BETWEEN_WAVES_ID = "majruszsdifficulty.undead_army.between_waves";
	static final String VICTORY_ID = "majruszsdifficulty.undead_army.victory";
	static final String FAILED_ID = "majruszsdifficulty.undead_army.failed";
	static final String APPROACHING_ID = "majruszsdifficulty.undead_army.approaching";

	public static MutableComponent buildWaveMessage( int currentWave ) {
		MutableComponent message = Component.translatable( TITLE_ID );

		return currentWave > 0 ? message.append( " " ).append( Component.translatable( WAVE_ID, currentWave ) ) : message;
	}

	public static MutableComponent buildBetweenWavesMessage() {
		return Component.translatable( BETWEEN_WAVES_ID );
	}

	public static MutableComponent buildVictoryMessage() {
		return Component.translatable( VICTORY_ID );
	}

	public static MutableComponent buildFailedMessage() {
		return Component.translatable( FAILED_ID );
	}

	public static MutableComponent buildApproachingMessage( Direction direction ) {
		String directionId = String.format( "majruszsdifficulty.undead_army.%s", direction.toString().toLowerCase() );

		return Component.translatable( APPROACHING_ID, Component.translatable( directionId ) )
			.withStyle( ChatFormatting.BOLD )
			.withStyle( ChatFormatting.DARK_PURPLE );
	}
}

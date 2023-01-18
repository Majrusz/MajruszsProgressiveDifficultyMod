package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ComponentBuilder {
	public static Component buildWaveComponent( Data data ) {
		return switch( data.phase ) {
			case CREATED -> Component.translatable( "majruszsdifficulty.undead_army.title" );
			case WAVE_PREPARING -> Component.translatable( "majruszsdifficulty.undead_army.between_waves" );
			case WAVE_ONGOING -> Component.translatable( "majruszsdifficulty.undead_army.title" )
				.append( " " )
				.append( Component.translatable( "majruszsdifficulty.undead_army.wave", data.currentWave ) );
			case UNDEAD_DEFEATED -> Component.translatable( "majruszsdifficulty.undead_army.victory" );
			case UNDEAD_WON -> Component.translatable( "majruszsdifficulty.undead_army.failed" );
			default -> CommonComponents.EMPTY;
		};
	}

	public static Component buildApproachingComponent( Data data ) {
		String directionId = String.format( "majruszsdifficulty.undead_army.%s", data.direction.toString().toLowerCase() );

		return Component.translatable( "majruszsdifficulty.undead_army.approaching", Component.translatable( directionId ) )
			.withStyle( ChatFormatting.BOLD )
			.withStyle( ChatFormatting.DARK_PURPLE );
	}
}

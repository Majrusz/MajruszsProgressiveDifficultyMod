package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ComponentBuilder {
	public static Component buildTitleComponent() {
		return Component.translatable( "majruszsdifficulty.undead_army.title" );
	}

	public static Component buildTitleComponent( Data data ) {
		return Component.translatable( "majruszsdifficulty.undead_army.title" )
			.append( " " )
			.append( Component.translatable( "majruszsdifficulty.undead_army.wave", data.currentWave ) );
	}

	public static Component buildPreparingComponent() {
		return Component.translatable( "majruszsdifficulty.undead_army.between_waves" );
	}

	public static Component buildDefeatedComponent() {
		return Component.translatable( "majruszsdifficulty.undead_army.failed" );
	}

	public static Component buildVictoryComponent() {
		return Component.translatable( "majruszsdifficulty.undead_army.victory" );
	}

	public static Component buildApproachingComponent( Data data ) {
		String directionId = String.format( "majruszsdifficulty.undead_army.%s", data.direction.toString().toLowerCase() );

		return Component.translatable( "majruszsdifficulty.undead_army.approaching", Component.translatable( directionId ) )
			.withStyle( ChatFormatting.BOLD )
			.withStyle( ChatFormatting.DARK_PURPLE );
	}
}

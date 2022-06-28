package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class UndeadArmyText {
	public static final MutableComponent TITLE = Component.translatable( "majruszsdifficulty.undead_army.title" );
	public static final MutableComponent WAVE = Component.translatable( "majruszsdifficulty.undead_army.wave" );
	public static final MutableComponent BETWEEN_WAVES = Component.translatable( "majruszsdifficulty.undead_army.between_waves" );
	public static final MutableComponent VICTORY = Component.translatable( "majruszsdifficulty.undead_army.victory" );
	public static final MutableComponent FAILED = Component.translatable( "majruszsdifficulty.undead_army.failed" );
	public static final MutableComponent APPROACHING = Component.translatable( "majruszsdifficulty.undead_army.approaching" );

	public static MutableComponent constructWaveMessage( int currentWave ) {
		return Component.literal( "" ).append( TITLE ).append( " (" ).append( WAVE ).append( " " + currentWave + ")" );
	}

	public static MutableComponent constructDirectionMessage( Direction direction ) {
		return APPROACHING.copy()
			.append( " " )
			.append( Component.translatable( "majruszsdifficulty.undead_army." + direction.toString().toLowerCase() ) )
			.append( "!" )
			.withStyle( ChatFormatting.BOLD )
			.withStyle( ChatFormatting.DARK_PURPLE );
	}
}

package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;

public class UndeadArmyText {
	public static final MutableComponent TITLE = new TranslatableComponent( "majruszsdifficulty.undead_army.title" );
	public static final MutableComponent WAVE = new TranslatableComponent( "majruszsdifficulty.undead_army.wave" );
	public static final MutableComponent BETWEEN_WAVES = new TranslatableComponent( "majruszsdifficulty.undead_army.between_waves" );
	public static final MutableComponent VICTORY = new TranslatableComponent( "majruszsdifficulty.undead_army.victory" );
	public static final MutableComponent FAILED = new TranslatableComponent( "majruszsdifficulty.undead_army.failed" );
	public static final MutableComponent APPROACHING = new TranslatableComponent( "majruszsdifficulty.undead_army.approaching" );

	public static MutableComponent constructWaveMessage( int currentWave ) {
		return new TextComponent( "" ).append( TITLE ).append( " (" ).append( WAVE ).append( " " + currentWave + ")" );
	}

	public static MutableComponent constructDirectionMessage( Direction direction ) {
		return APPROACHING.copy()
			.append( " " )
			.append( new TranslatableComponent( "majruszsdifficulty.undead_army." + direction.toString().toLowerCase() ) )
			.append( "!" )
			.withStyle( ChatFormatting.BOLD )
			.withStyle( ChatFormatting.DARK_PURPLE );
	}
}

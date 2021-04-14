package com.majruszs_difficulty;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Predicate;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszs_difficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";
	public static final String VERSION = "0.5.1";
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "majruszs-difficulty-common.toml" );
	public static final ConfigGroup FEATURES_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Features", "" ) );
	public static final ConfigGroup ENTITIES_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Entities", "" ) );
	public static final ConfigGroup STRUCTURES_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Structures", "" ) );
	public static final ConfigGroup STATE_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "GameState", "" ) );

	public MajruszsDifficulty() {
		RegistryHandler.init();

		MinecraftForge.EVENT_BUS.register( this );
	}

	/** Returns resource location for register in current modification files. */
	public static ResourceLocation getLocation( String register ) {
		return new ResourceLocation( MOD_ID, register );
	}

	/** Returns formatted text with information that item is disabled. */
	public static IFormattableTextComponent getDisabledItemTooltip() {
		return new TranslationTextComponent( "majruszs_difficulty.items.disabled_tooltip" ).withStyle( TextFormatting.RED, TextFormatting.BOLD );
	}

	/** Adds information that item is disabled if certain conditions are met. */
	public static void addExtraTooltipIfDisabled( List< ITextComponent > toolTip, boolean isEnabled ) {
		if( !isEnabled )
			toolTip.add( getDisabledItemTooltip() );
	}
}

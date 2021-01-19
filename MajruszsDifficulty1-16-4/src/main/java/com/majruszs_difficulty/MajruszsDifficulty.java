package com.majruszs_difficulty;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszs_difficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";
	public static final String VERSION = "0.4.0";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Random RANDOM = new Random();
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "majruszs-difficulty-new.toml" );
	public static final ConfigGroup ENCHANTMENT_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Enchantments", "" ) );
	public static final ConfigGroup CURSE_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Curses", "" ) );

	public MajruszsDifficulty() {
		RegistryHandler.init();

		ConfigHandlerOld.register( ModLoadingContext.get() );

		MinecraftForge.EVENT_BUS.register( this );
	}

	public static ResourceLocation getLocation( String register ) {
		return new ResourceLocation( MOD_ID, register );
	}
}

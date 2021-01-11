package com.majruszs_difficulty;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public MajruszsDifficulty() {
		RegistryHandler.init();

		ConfigHandler.register( ModLoadingContext.get() );

		MinecraftForge.EVENT_BUS.register( this );
	}

	public static final String
		MOD_ID      = "majruszs_difficulty",
		NAME        = "Majrusz's Progressive Difficulty",
		VERSION     = "0.3.0";

	public static final Logger LOGGER = LogManager.getLogger();
	public static final Random RANDOM = new Random();
}

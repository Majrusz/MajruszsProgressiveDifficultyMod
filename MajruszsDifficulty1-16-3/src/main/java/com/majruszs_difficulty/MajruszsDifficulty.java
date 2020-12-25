package com.majruszs_difficulty;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public MajruszsDifficulty() {
		RegistryHandler.init();

		MinecraftForge.EVENT_BUS.register( this );
	}

	public static final String
		MOD_ID      = "majruszs_difficulty",
		NAME        = "Majrusz's Progressive Difficulty",
		VERSION     = "0.1.2";

	public static final Logger LOGGER = LogManager.getLogger();
	public static final Random RANDOM = new Random();
}

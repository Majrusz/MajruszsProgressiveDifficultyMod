package com.majruszsdifficulty;

import com.mlib.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";
	public static final ConfigHandler SERVER_CONFIG = new ConfigHandler( ModConfig.Type.SERVER );

	public MajruszsDifficulty() {
		com.majruszsdifficulty.Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}

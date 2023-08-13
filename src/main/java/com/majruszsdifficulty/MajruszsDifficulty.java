package com.majruszsdifficulty;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";

	public MajruszsDifficulty() {
		com.majruszsdifficulty.Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}

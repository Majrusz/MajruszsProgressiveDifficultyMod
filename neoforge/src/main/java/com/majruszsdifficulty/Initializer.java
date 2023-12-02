package com.majruszsdifficulty;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod( MajruszsDifficulty.MOD_ID )
public class Initializer {
	public Initializer() {
		MajruszsDifficulty.HELPER.register();
		MinecraftForge.EVENT_BUS.register( this );
	}
}

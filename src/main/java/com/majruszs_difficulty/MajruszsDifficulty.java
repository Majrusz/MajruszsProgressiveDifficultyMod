package com.majruszs_difficulty;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszs_difficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";
	public static final String VERSION = "1.2.1";
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
}

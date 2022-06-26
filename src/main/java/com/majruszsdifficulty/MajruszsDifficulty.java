package com.majruszsdifficulty;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/** Main modification class. */
@Mod( MajruszsDifficulty.MOD_ID )
public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final String NAME = "Majrusz's Progressive Difficulty";
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "common.toml", MOD_ID );
	public static final ConfigGroup GAME_MODIFIERS_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( GameModifier.DEFAULT );
	public static final ConfigGroup UNDEAD_ARMY_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( GameModifier.UNDEAD_ARMY, "UndeadArmy", "" );
	public static final ConfigGroup STAGE_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( GameModifier.GAME_STAGE, "GameStage", "" );
	public static final ConfigGroup TREASURE_BAG_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( GameModifier.TREASURE_BAG, "TreasureBag", "" );
	public static final ConfigGroup ENTITIES_GROUP = CONFIG_HANDLER.addNewGroup( "Entities", "" );
	public static final ConfigGroup STATE_GROUP = CONFIG_HANDLER.addNewGroup( "GameStage", "" );

	public MajruszsDifficulty() {
		Registries.initialize();

		MinecraftForge.EVENT_BUS.register( this );
	}
}

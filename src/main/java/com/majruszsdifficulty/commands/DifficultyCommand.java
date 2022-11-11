package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.commands.Command;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DifficultyCommand extends Command {
	static final String GAME_STAGE_ID = "gamestage";

	protected MutableComponent createGameStageMessage( GameStage.Stage stage, String translationKey ) {
		return Component.translatable( "commands.gamestage." + translationKey, GameStage.getGameStageText( stage ) );
	}
}

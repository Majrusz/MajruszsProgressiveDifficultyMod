package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.commands.Command;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

// TODO: move all commands to separate packages
public class DifficultyCommand extends Command {
	protected static MutableComponent createGameStageMessage( GameStage stage, String translationKey ) {
		return Component.translatable( "commands.gamestage." + translationKey, GameStage.getGameStageText( stage ) );
	}

	protected static String asVec3i( BlockPos position ) {
		return String.format( "(%d, %d, %d)", ( int )( position.getX() ), ( int )( position.getY() ), ( int )( position.getZ() ) );
	}
}

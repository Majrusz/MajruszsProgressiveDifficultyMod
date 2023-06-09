package com.majruszsdifficulty.gamestage.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mlib.levels.LevelHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

@AutoInstance
public class ClampedRegionalDifficultyGetCommand extends Command {
	public ClampedRegionalDifficultyGetCommand() {
		this.newBuilder()
			.literal( "crd", "clampedregionaldifficulty" )
			.execute( this::handle )
			.entity()
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		MutableComponent message = this.buildMessage( data );
		data.source.sendSuccess( ()->message, true );

		return 0;
	}

	private MutableComponent buildMessage( CommandData data ) throws CommandSyntaxException {
		ServerLevel level = data.source.getLevel();
		BlockPos position = this.getOptionalEntityOrPlayer( data ).blockPosition();
		String total = String.format( "%.2f", LevelHelper.getClampedRegionalDifficultyAt( level, position ) );

		return Component.translatable( "commands.clampedregionaldifficulty", String.format( "(%s)", position.toShortString() ), this.withStageStyle( total ) );
	}

	private MutableComponent withStageStyle( String text ) {
		return Component.literal( text ).withStyle( GameStage.getCurrentStage().getChatFormatting() );
	}
}

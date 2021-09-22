package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mlib.commands.BaseCommand;
import com.mlib.commands.IRegistrableCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Stores instances of all commands. */
public class CommandsHelper {
	public static final List< IRegistrableCommand > COMMANDS = new ArrayList<>();
	public final static LiteralArgumentBuilder< CommandSourceStack > UNDEAD_ARMY_ARGUMENT = BaseCommand.literal( "undeadarmy" );
	static {
		COMMANDS.add( new StopUndeadArmyCommands() );
		COMMANDS.add( new StartUndeadArmyCommands() );
		COMMANDS.add( new KillUndeadArmyCommands() );
		COMMANDS.add( new HighlightUndeadArmyCommands() );
		COMMANDS.add( new UndeadArmyProgressCommands() );
		COMMANDS.add( new ChangeGameStageCommand() );
		COMMANDS.add( new GetGameStageCommand() );
		COMMANDS.add( new GetClampedRegionalDifficultyCommands() );
	}

	public static MutableComponent createGameStageMessage( GameState.State state, String translationID ) {
		return new TranslatableComponent( "commands.gamestage." + translationID, GameState.getGameStateText( state ) );
	}

	public static MutableComponent createBaseMessageWithPosition( String translationKey, Vec3 position, Object... objects ) {
		Object[] copy = new Object[ objects.length + 1 ];
		copy[ objects.length ] = getPositionFormatted( position );
		System.arraycopy( objects, 0, copy, 0, objects.length );

		return new TranslatableComponent( translationKey, copy );
	}

	public static String getPositionFormatted( Vec3 position ) {
		return String.format( "(%d, %d, %d)", ( int )( position.x ), ( int )( position.y ), ( int )( position.z ) );
	}
}

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
		COMMANDS.add( new ChangeGameStageCommand() );
		COMMANDS.add( new GetGameStageCommand() );
	}

	public static MutableComponent createGameStageMessage( GameState.State state, String translationID ) {
		MutableComponent feedback = new TranslatableComponent( "commands.gamestage." + translationID );
		feedback.append( " " );
		feedback.append( GameState.getGameStateText( state ) );
		feedback.append( "!" );

		return feedback;
	}

	public static MutableComponent createBaseMessage( String translationKey ) {
		return new TranslatableComponent( translationKey );
	}

	public static MutableComponent createBaseMessageWithPosition( String translationKey, Vec3 vec3 ) {
		MutableComponent feedback = new TranslatableComponent( translationKey );
		feedback.append( ": " );
		feedback.append( "(x=" + ( int )( vec3.x ) + ", y=" + ( int )( vec3.y ) + ", z=" + ( int )( vec3.z ) + ")" );

		return feedback;
	}
}

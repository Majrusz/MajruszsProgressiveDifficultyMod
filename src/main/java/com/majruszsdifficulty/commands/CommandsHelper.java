package com.majruszsdifficulty.commands;

import com.mlib.commands.BaseCommand;
import com.mlib.commands.IRegistrableCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Stores instances of all commands. */
@Deprecated
public class CommandsHelper {
	public static final List< IRegistrableCommand > COMMANDS = new ArrayList<>();
	public final static LiteralArgumentBuilder< CommandSourceStack > UNDEAD_ARMY_ARGUMENT = BaseCommand.literal( "undeadarmy" );
	public final static LiteralArgumentBuilder< CommandSourceStack > TREASURE_BAG_ARGUMENT = BaseCommand.literal( "treasurebag" );

	static {
		COMMANDS.add( new StopUndeadArmyCommands() );
		COMMANDS.add( new StartUndeadArmyCommands() );
		COMMANDS.add( new KillUndeadArmyCommands() );
		COMMANDS.add( new HighlightUndeadArmyCommands() );
		COMMANDS.add( new UndeadArmyUndeadLeftCommands() );
		COMMANDS.add( new UndeadArmyProgressCommands() );
		COMMANDS.add( new TreasureBagResetProgressCommands() );
	}

	public static MutableComponent createBaseMessageWithPosition( String translationKey, Vec3 position, Object... objects ) {
		Object[] copy = new Object[ objects.length + 1 ];
		copy[ objects.length ] = getPositionFormatted( position );
		System.arraycopy( objects, 0, copy, 0, objects.length );

		return Component.translatable( translationKey, copy );
	}

	public static String getPositionFormatted( Vec3 position ) {
		return String.format( "(%d, %d, %d)", ( int )( position.x ), ( int )( position.y ), ( int )( position.z ) );
	}
}

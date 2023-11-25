package com.majruszsdifficulty.undeadarmy;

import com.majruszlibrary.command.Command;
import com.majruszlibrary.command.CommandData;
import com.majruszlibrary.command.IParameter;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.listeners.UndeadArmyTrigger;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UndeadArmyCommands {
	private static final IParameter< UndeadArmy.Direction > DIRECTION = Command.enumeration( UndeadArmy.Direction::values ).named( "direction" );
	private static final IParameter< List< Vec3 > > POSITIONS = Command.anyPosition();
	private static final IParameter< Entity > ENTITY = Command.entity();

	static {
		Command.create()
			.literal( "undeadarmy" )
			.hasPermission( 4 )
			.literal( "start" )
			.execute( UndeadArmyCommands::start )
			.parameter( DIRECTION )
			.execute( UndeadArmyCommands::start )
			.parameter( POSITIONS )
			.execute( UndeadArmyCommands::start )
			.register();

		Command.create()
			.literal( "undeadarmy" )
			.hasPermission( 4 )
			.literal( "listall" )
			.execute( UndeadArmyCommands::listAll )
			.register();

		Command.create()
			.literal( "undeadarmy" )
			.hasPermission( 4 )
			.literal( "progress" )
			.execute( UndeadArmyCommands::sendProgress )
			.parameter( ENTITY )
			.execute( UndeadArmyCommands::sendProgress )
			.register();

		UndeadArmyCommands.create( "highlight", "highlighted", UndeadArmy::highlight );
		UndeadArmyCommands.create( "killall", "killed", UndeadArmy::killAllMobs );
		UndeadArmyCommands.create( "stop", "finished", UndeadArmy::finish );
	}

	private static int start( CommandData data ) throws CommandSyntaxException {
		Optional< UndeadArmy.Direction > direction = data.getOptional( DIRECTION );
		List< Vec3 > positions = data.getOptional( POSITIONS ).orElseGet( List::of );
		if( positions.isEmpty() ) {
			positions.add( data.getCaller().position() );
		}

		for( Vec3 position : positions ) {
			BlockPos blockPos = AnyPos.from( position ).block();
			if( UndeadArmyHelper.tryToSpawn( blockPos, direction ) ) {
				data.source.sendSuccess( ()->TextHelper.translatable( "commands.undeadarmy.started", "(%s)".formatted( blockPos.toShortString() ) ), true );
			} else {
				data.source.sendFailure( TextHelper.translatable( "commands.undeadarmy.cannot_start", "(%s)".formatted( blockPos.toShortString() ) ) );
			}
		}

		return 0;
	}

	private static int listAll( CommandData data ) throws CommandSyntaxException {
		List< UndeadArmy > undeadArmies = UndeadArmyHelper.getUndeadArmies();
		if( !undeadArmies.isEmpty() ) {
			MutableComponent component = TextHelper.translatable( "commands.undeadarmy.list" );
			undeadArmies.forEach( undeadArmy->{
				component.append( "\n- " );
				component.append( TextHelper.translatable( "majruszsdifficulty.undead_army.title" ) );
				component.append( TextHelper.empty() );
				component.append( TextHelper.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( Math.max( undeadArmy.currentWave, 1 ) ) ) );
				component.append( " (%s)".formatted( undeadArmy.position.toShortString() ) );
			} );
			data.source.sendSuccess( ()->component, true );
			return 0;
		}

		data.source.sendFailure( TextHelper.translatable( "commands.undeadarmy.list_empty" ) );
		return -1;
	}

	private static int sendProgress( CommandData data ) throws CommandSyntaxException {
		Entity entity = data.getOptional( ENTITY ).orElseGet( data::getCaller );
		CompoundTag tag = EntityHelper.getExtraTag( entity );
		int undeadLeft = tag != null ? Serializables.read( new UndeadArmyTrigger.Progress(), tag ).undeadLeft : UndeadArmyConfig.KILL_REQUIREMENT_FIRST;

		data.source.sendSuccess( ()->Component.translatable( "commands.undeadarmy.progress", entity.getDisplayName(), Math.max( undeadLeft, 1 ) ), true );
		return 0;
	}

	private static void create( String command, String successId, Consumer< UndeadArmy > consumer ) {
		Command.create()
			.literal( "undeadarmy" )
			.hasPermission( 4 )
			.literal( command )
			.execute( data->UndeadArmyCommands.handle( data, successId, consumer ) )
			.parameter( POSITIONS )
			.execute( data->UndeadArmyCommands.handle( data, successId, consumer ) )
			.register();
	}

	private static int handle( CommandData data, String successId, Consumer< UndeadArmy > consumer ) {
		List< Vec3 > positions = data.getOptional( POSITIONS ).orElseGet( List::of );
		if( positions.isEmpty() ) {
			positions.add( data.getCaller().position() );
		}

		for( Vec3 position : positions ) {
			BlockPos blockPos = AnyPos.from( position ).block();
			UndeadArmy undeadArmy = UndeadArmyHelper.findNearestUndeadArmy( blockPos );
			if( undeadArmy != null ) {
				consumer.accept( undeadArmy );
				data.source.sendSuccess( ()->TextHelper.translatable( "commands.undeadarmy." + successId, "(%s)".formatted( blockPos.toShortString() ) ), true );
			} else {
				data.source.sendFailure( TextHelper.translatable( "commands.undeadarmy.missing", "(%s)".formatted( blockPos.toShortString() ) ) );
			}
		}

		return 0;
	}
}

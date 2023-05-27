package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class UndeadArmyCommand extends Command {
	final BiFunction< UndeadArmy, BlockPos, Component > componentSupplier;

	public UndeadArmyCommand( String command, BiFunction< UndeadArmy, BlockPos, Component > componentSupplier ) {
		this.componentSupplier = componentSupplier;

		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( command )
			.hasPermission( 4 )
			.execute( this::handle )
			.entity()
			.execute( this::handle );
	}

	public UndeadArmyCommand( String command, String successId, Consumer< UndeadArmy > consumer ) {
		this( command, ( undeadArmy, position )->{
			consumer.accept( undeadArmy );
			return new TranslatableComponent( "commands.undeadarmy." + successId, String.format( "(%s)", position.toShortString() ) );
		} );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		BlockPos position = this.getOptionalEntityOrPlayer( data ).blockPosition();
		UndeadArmy undeadArmy = Registries.getUndeadArmyManager().findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			data.source.sendSuccess( this.componentSupplier.apply( undeadArmy, position ), true );
			return 0;
		}

		data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.missing", String.format( "(%s)", position.toShortString() ) ), true );
		return -1;
	}
}

package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.commands.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class UndeadArmyCommand extends DifficultyCommand {
	final BiFunction< UndeadArmy, Vec3, Component > componentSupplier;

	public UndeadArmyCommand( String command, BiFunction< UndeadArmy, Vec3, Component > componentSupplier ) {
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
			return new TranslatableComponent( "commands.undeadarmy." + successId, asVec3i( position ) );
		} );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		Vec3 position = this.getOptionalEntityOrPlayer( data ).position();
		UndeadArmy undeadArmy = Registries.getUndeadArmyManager().findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			data.source.sendSuccess( this.componentSupplier.apply( undeadArmy, position ), true );
			return 0;
		}

		data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.missing", asVec3i( position ) ), true );
		return -1;
	}
}

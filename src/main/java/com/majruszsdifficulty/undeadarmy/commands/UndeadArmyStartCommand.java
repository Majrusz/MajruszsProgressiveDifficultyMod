package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.data.Direction;
import com.mlib.modhelper.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

@AutoInstance
public class UndeadArmyStartCommand extends Command {
	public UndeadArmyStartCommand() {
		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( "start" )
			.hasPermission( 4 )
			.execute( this::handle )
			.enumeration( Direction.class )
			.execute( this::handle )
			.entity()
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		Optional< Direction > direction = this.getOptionalEnumeration( data, Direction.class );
		BlockPos position = this.getOptionalEntityOrPlayer( data ).blockPosition();
		if( Registries.getUndeadArmyManager().tryToSpawn( new BlockPos( position ), direction ) ) {
			data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.started", String.format( "(%s)", position.toShortString() ) ), true );
			return 0;
		}

		data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.cannot_start", String.format( "(%s)", position.toShortString() ) ), true );
		return -1;
	}
}

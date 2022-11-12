package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

@AutoInstance
public class StopUndeadArmyCommand extends DifficultyCommand {
	public StopUndeadArmyCommand() {
		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( "stop" )
			.hasPermission( 4 )
			.execute( this::handle )
			.entity()
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		Vec3 position = this.getOptionalEntityOrPlayer( data ).position();
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( position ) );

		if( undeadArmy != null ) {
			undeadArmy.finish();
			data.source.sendSuccess( Component.translatable( "commands.undeadarmy.finished", this.asVec3i( position ) ), true );
			return 0;
		}

		data.source.sendSuccess( Component.translatable( "commands.undeadarmy.missing", this.asVec3i( position ) ), true );
		return -1;
	}
}

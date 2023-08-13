package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.Config;
import com.majruszsdifficulty.undeadarmy.data.UndeadArmyInfo;
import com.mlib.modhelper.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@AutoInstance
public class UndeadArmyPersonalProgressCommand extends Command {
	public UndeadArmyPersonalProgressCommand() {
		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( "progress" )
			.hasPermission( 4 )
			.execute( this::handle )
			.entity()
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		Config config = Registries.getUndeadArmyManager().getConfig();
		Entity entity = this.getOptionalEntityOrPlayer( data );
		UndeadArmyInfo info = config.readUndeadArmyInfo( entity.getPersistentData() );
		int killsToStart = Math.max( config.getRequiredKills() - info.killedUndead, 1 );

		data.source.sendSuccess( Component.translatable( "commands.undeadarmy.progress", entity.getDisplayName(), killsToStart ), true );
		return -1;
	}
}

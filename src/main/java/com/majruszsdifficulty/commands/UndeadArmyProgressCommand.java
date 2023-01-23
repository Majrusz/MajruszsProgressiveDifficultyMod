package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyKeys;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;
import com.mlib.nbt.NBTHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.LivingEntity;

@AutoInstance
public class UndeadArmyProgressCommand extends DifficultyCommand {
	public UndeadArmyProgressCommand() {
		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( "progress" )
			.hasPermission( 4 )
			.execute( this::handle )
			.entity()
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		if( this.getOptionalEntityOrPlayer( data ) instanceof LivingEntity entity ) {
			NBTHelper.IntegerData undeadKilledData = new NBTHelper.IntegerData( entity, UndeadArmyKeys.KILLED );
			int undeadKilled = undeadKilledData.get();
			int undeadKilledMax = UndeadArmyConfig.getRequiredKills();

			data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.progress", entity.getName(), undeadKilled, undeadKilledMax ), true );
			return 0;
		}

		return -1;
	}
}

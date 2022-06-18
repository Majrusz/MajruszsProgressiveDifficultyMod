package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.features.undead_army.UndeadArmyKeys;
import com.mlib.Utility;
import com.mlib.commands.EntityCommand;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.nbt.NBTHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Command that sends information about already killed undead. */
public class UndeadArmyProgressCommands extends EntityCommand implements IRegistrableCommand {
	@Override
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source, Entity entity ) {
		LivingEntity livingEntity = Utility.castIfPossible( LivingEntity.class, entity );
		if( livingEntity != null ) {
			NBTHelper.IntegerData undeadKilledData = new NBTHelper.IntegerData( livingEntity, UndeadArmyKeys.KILLED );
			int undeadKilled = undeadKilledData.get();
			int undeadKilledMax = Registries.UNDEAD_ARMY_CONFIG.getRequiredKills();

			source.sendSuccess( Component.translatable( "commands.undeadarmy.progress", entity.getName(), undeadKilled, undeadKilledMax ), true );
			return 0;
		}

		return -1;
	}

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		Data commandData = new Data( hasPermission( 4 ), CommandsHelper.UNDEAD_ARMY_ARGUMENT, literal( "progress" ) );
		registerEntityCommand( commandDispatcher, commandData );
	}
}

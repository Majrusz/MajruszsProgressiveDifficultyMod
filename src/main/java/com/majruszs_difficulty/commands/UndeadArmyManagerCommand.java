package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.features.undead_army.UndeadArmy;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

/** All commands for undead army. */
public final class UndeadArmyManagerCommand {
	private UndeadArmyManagerCommand() {}

	public static void register( CommandDispatcher< CommandSourceStack > dispatcher ) {
		dispatcher.register( Commands.literal( "undead_army" )
			.requires( source->source.hasPermission( 4 ) )
			.then( Commands.literal( "stop" )
				.executes( entity->stopUndeadArmy( entity.getSource() ) ) )
			.then( Commands.literal( "highlight" )
				.executes( entity->highlightUndeadArmy( entity.getSource() ) ) )
			.then( Commands.literal( "undead_left" )
				.executes( entity->countUndeadLeft( entity.getSource() ) ) )
			.then( Commands.literal( "kill_all" )
				.executes( entity->killUndeadArmy( entity.getSource() ) ) ) );
	}

	/** Command responsible for stopping Undead Army in the player position. */
	public static int stopUndeadArmy( CommandSourceStack source ) {
		if( RegistryHandler.UNDEAD_ARMY_MANAGER == null )
			return -1;

		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( source.getPosition() ) );
		if( undeadArmy == null ) {
			source.sendSuccess( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.finish();
		return 0;
	}

	/** Command responsible for highlighting all Undead Army units in the player position. */
	public static int highlightUndeadArmy( CommandSourceStack source ) {
		if( RegistryHandler.UNDEAD_ARMY_MANAGER == null )
			return -1;

		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( source.getPosition() ) );
		if( undeadArmy == null ) {
			source.sendSuccess( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.highlightUndeadArmy();
		return 0;
	}

	/** Command responsible for informing the player how many Undead Army units left in the player position. */
	public static int countUndeadLeft( CommandSourceStack source ) {
		if( RegistryHandler.UNDEAD_ARMY_MANAGER == null )
			return -1;

		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( source.getPosition() ) );
		if( undeadArmy == null ) {
			source.sendSuccess( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		MutableComponent feedback = new TranslatableComponent( "commands.undead_army.undead_left" );
		feedback.append( " " + undeadArmy.countUndeadEntitiesLeft() + "." );
		source.sendSuccess( feedback, true );
		return 0;
	}

	/** Command responsible for killing all Undead Army entities. */
	public static int killUndeadArmy( CommandSourceStack source ) {
		if( RegistryHandler.UNDEAD_ARMY_MANAGER == null )
			return -1;

		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( source.getPosition() ) );
		if( undeadArmy == null ) {
			source.sendSuccess( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.killAllUndeadArmyEntities();
		return 0;
	}

	private static MutableComponent getMissingUndeadArmyFeedback() {
		return new TranslatableComponent( "commands.undead_army.missing" );
	}
}

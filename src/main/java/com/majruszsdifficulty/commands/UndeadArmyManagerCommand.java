package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.RegistryHandler;
import com.majruszsdifficulty.events.undead_army.UndeadArmy;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/** All commands for undead army. */
public final class UndeadArmyManagerCommand {
	private UndeadArmyManagerCommand() {}

	public static void register( CommandDispatcher< CommandSource > dispatcher ) {
		dispatcher.register( Commands.literal( "undead_army" )
			.requires( source->source.hasPermissionLevel( 4 ) )
			.then( Commands.literal( "stop" )
				.executes( entity->stopUndeadArmy( entity.getSource() ) ) )
			.then( Commands.literal( "highlight" )
				.executes( entity->highlightUndeadArmy( entity.getSource() ) ) )
			.then( Commands.literal( "undead_left" )
				.executes( entity->countUndeadLeft( entity.getSource() ) ) )
			.then( Commands.literal( "kill_all" )
				.executes( entity->killUndeadArmy( entity.getSource() ) ) )
		);
	}

	/** Command responsible for stopping Undead Army in the player position. */
	public static int stopUndeadArmy( CommandSource source ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( source.getPos() ) );
		if( undeadArmy == null ) {
			source.sendFeedback( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.finish();
		return 0;
	}

	/** Command responsible for highlighting all Undead Army units in the player position. */
	public static int highlightUndeadArmy( CommandSource source ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( source.getPos() ) );
		if( undeadArmy == null ) {
			source.sendFeedback( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.highlightUndeadArmy();
		return 0;
	}

	/** Command responsible for informing the player how many Undead Army units left in the player position. */
	public static int countUndeadLeft( CommandSource source ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( source.getPos() ) );
		if( undeadArmy == null ) {
			source.sendFeedback( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		IFormattableTextComponent feedback = new TranslationTextComponent( "commands.undead_army.undead_left" );
		feedback.appendString( " " + undeadArmy.countUndeadEntitiesLeft() + "." );
		source.sendFeedback( feedback, true );
		return 0;
	}

	/** Command responsible for killing all Undead Army entities. */
	public static int killUndeadArmy( CommandSource source ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( source.getPos() ) );
		if( undeadArmy == null ) {
			source.sendFeedback( getMissingUndeadArmyFeedback(), true );
			return -1;
		}

		undeadArmy.killAllUndeadArmyEntities();
		return 0;
	}

	private static IFormattableTextComponent getMissingUndeadArmyFeedback() {
		return new TranslationTextComponent( "commands.undead_army.missing" );
	}
}

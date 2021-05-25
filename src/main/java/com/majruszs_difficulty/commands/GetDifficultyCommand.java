package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mlib.WorldHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/** Command responsible for informing about current game regional difficulty multiplier. */
public final class GetDifficultyCommand {
	private GetDifficultyCommand() {}

	public static void register( CommandDispatcher< CommandSource > dispatcher ) {
		dispatcher.register( Commands.literal( "regionaldifficulty" )
			.requires( source->source.hasPermissionLevel( 0 ) )
			.executes( entity->getCurrentDifficulty( entity.getSource() ) ) );
	}

	/** Sends feedback message to entity. */
	public static int getCurrentDifficulty( CommandSource source ) {
		source.sendFeedback( getFeedbackMessage( source.getEntity() ), true );

		return 0;
	}

	/** Returns full formatted feedback message. */
	private static IFormattableTextComponent getFeedbackMessage( Entity entity ) {
		IFormattableTextComponent feedback = new TranslationTextComponent( "commands.regional_difficulty" );
		feedback.append( getStateModifierText( ( LivingEntity )entity ) );

		return feedback;
	}

	/** Returns formatted regional difficulty. */
	private static IFormattableTextComponent getStateModifierText( @Nullable LivingEntity target ) {
		if( target == null )
			return new StringTextComponent( "invalid target" ).mergeStyle( TextFormatting.RED );

		IFormattableTextComponent text = new StringTextComponent( " " + String.format( "%.2f", WorldHelper.getClampedRegionalDifficulty( target ) ) );
		IFormattableTextComponent stateText = new StringTextComponent(
			"+ " + GameState.getStateModifier() + " = " + String.format( "%.2f", GameState.getRegionalDifficulty( target ) ) );
		switch( GameState.getCurrentMode() ) {
			case EXPERT:
				stateText.mergeStyle( GameState.EXPERT_MODE_COLOR, TextFormatting.BOLD );
				text.appendString( " (" );
				text.append( stateText );
				text.appendString( ")" );

				return text;
			case MASTER:
				stateText.mergeStyle( GameState.MASTER_MODE_COLOR, TextFormatting.BOLD );
				text.appendString( " (" );
				text.append( stateText );
				text.appendString( ")" );

				return text;
			default:
				return text;
		}
	}
}

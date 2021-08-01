package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mlib.LevelHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Command responsible for informing about current game regional difficulty multiplier. */
public final class GetDifficultyCommand {
	private GetDifficultyCommand() {}

	public static void register( CommandDispatcher< CommandSourceStack > dispatcher ) {
		dispatcher.register( Commands.literal( "clampedregionaldifficulty" )
			.requires( source->source.hasPermission( 0 ) )
			.executes( entity->getCurrentDifficulty( entity.getSource() ) ) );

		dispatcher.register( Commands.literal( "crd" )
			.requires( source->source.hasPermission( 0 ) )
			.executes( entity->getCurrentDifficulty( entity.getSource() ) ) );
	}

	/** Sends feedback message to entity. */
	public static int getCurrentDifficulty( CommandSourceStack source ) {
		source.sendSuccess( getFeedbackMessage( source.getEntity() ), true );

		return 0;
	}

	/** Returns fully formatted feedback message. */
	private static MutableComponent getFeedbackMessage( Entity entity ) {
		MutableComponent feedback = new TranslatableComponent( "commands.regional_difficulty" );
		feedback.append( getStateModifierText( ( LivingEntity )entity ) );

		return feedback;
	}

	/** Returns formatted regional difficulty. */
	private static MutableComponent getStateModifierText( @Nullable LivingEntity target ) {
		if( target == null )
			return new TextComponent( "invalid target" ).withStyle( ChatFormatting.RED );

		MutableComponent text = new TextComponent( " " + String.format( "%.2f", LevelHelper.getClampedRegionalDifficulty( target ) ) );
		MutableComponent stateText = new TextComponent(
			"+ " + GameState.getStateModifier() + " = " + String.format( "%.2f", GameState.getRegionalDifficulty( target ) ) );
		switch( GameState.getCurrentMode() ) {
			case EXPERT:
				stateText.withStyle( GameState.EXPERT_MODE_COLOR, ChatFormatting.BOLD );
				text.append( " (" );
				text.append( stateText );
				text.append( ")" );

				return text;
			case MASTER:
				stateText.withStyle( GameState.MASTER_MODE_COLOR, ChatFormatting.BOLD );
				text.append( " (" );
				text.append( stateText );
				text.append( ")" );

				return text;
			default:
				return text;
		}
	}
}

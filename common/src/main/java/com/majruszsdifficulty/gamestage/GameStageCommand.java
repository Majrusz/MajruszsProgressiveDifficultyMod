package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.command.Command;
import com.majruszlibrary.command.CommandData;
import com.majruszlibrary.command.IParameter;
import com.majruszlibrary.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameStageCommand {
	static final IParameter< String > GAME_STAGE = Command.string()
		.named( "name" )
		.suggests( ()->GameStageHelper.getGameStages().stream().map( GameStage::getId ).toList() );
	static final IParameter< List< ? extends Entity > > ENTITIES = Command.entities();

	static {
		Command.create()
			.literal( "gamestage", "gamestate" )
			.hasPermission( 4 )
			.parameter( GAME_STAGE )
			.execute( GameStageCommand::set )
			.parameter( ENTITIES )
			.execute( GameStageCommand::set )
			.register();

		Command.create()
			.literal( "gamestage", "gamestate" )
			.hasPermission( 4 )
			.execute( GameStageCommand::get )
			.parameter( ENTITIES )
			.execute( GameStageCommand::get )
			.register();
	}

	private static int set( CommandData data ) throws CommandSyntaxException {
		String name = data.get( GAME_STAGE );
		GameStage gameStage = GameStageHelper.getGameStages().stream().filter( stage->stage.is( name ) ).findFirst().orElseThrow();
		Optional< List< ? extends Entity > > entities = data.getOptional( ENTITIES );
		if( GameStageCommand.isInvalid( data, entities.isPresent() ) ) {
			return -1;
		}

		entities.map( GameStageCommand::toPlayers )
			.orElse( GameStageCommand.getNullList() )
			.forEach( player->{
				boolean hasGameStageChanged = player != null ? GameStageHelper.setGameStage( gameStage, player ) : GameStageHelper.setGlobalGameStage( gameStage );
				GameStageCommand.send( data, hasGameStageChanged ? "changed" : "cannot_change", player );
			} );

		return 0;
	}

	private static int get( CommandData data ) throws CommandSyntaxException {
		Optional< List< ? extends Entity > > entities = data.getOptional( ENTITIES );
		if( GameStageCommand.isInvalid( data, entities.isPresent() ) ) {
			return -1;
		}

		entities.map( GameStageCommand::toPlayers )
			.orElse( GameStageCommand.getNullList() )
			.forEach( player->GameStageCommand.send( data, "current", player ) );

		return 0;
	}

	private static void send( CommandData data, String id, @Nullable Player player ) {
		String messageId = "commands.gamestage.%s.%s".formatted( player != null ? "player" : "global", id );
		MutableComponent component;
		if( player != null ) {
			component = TextHelper.translatable( messageId, GameStageHelper.getGameStage( player ).getComponent(), player.getDisplayName() );
		} else {
			component = TextHelper.translatable( messageId, GameStageHelper.getGlobalGameStage().getComponent() );
		}

		data.source.sendSuccess( component, true );
	}

	private static boolean isInvalid( CommandData data, boolean arePlayersDefined ) {
		if( !GameStageHelper.isPerPlayerDifficultyEnabled() && arePlayersDefined ) {
			data.source.sendFailure( TextHelper.translatable( "commands.gamestage.player.disabled" ) );

			return true;
		}

		return false;
	}

	private static List< Player > toPlayers( List< ? extends Entity > entities ) {
		return entities.stream().filter( entity->entity instanceof Player ).map( Player.class::cast ).toList();
	}

	private static List< Player > getNullList() {
		ArrayList< Player > players = new ArrayList<>();
		players.add( null );

		return players;
	}
}

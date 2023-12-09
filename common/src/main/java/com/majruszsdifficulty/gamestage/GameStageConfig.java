package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import net.minecraft.ChatFormatting;

import java.util.List;

public class GameStageConfig {
	public static boolean IS_PER_PLAYER_DIFFICULTY_ENABLED = false;
	public static List< GameStage > GAME_STAGES = GameStageConfig.updateOrdinals( List.of(
		GameStage.named( GameStage.NORMAL_ID )
			.format( ChatFormatting.WHITE )
			.create(),
		GameStage.named( GameStage.EXPERT_ID )
			.format( ChatFormatting.RED, ChatFormatting.BOLD )
			.triggersIn( "{regex}.*" )
			.message( "majruszsdifficulty.stages.expert.started", ChatFormatting.RED, ChatFormatting.BOLD )
			.message( "majruszsdifficulty.undead_army.on_expert", ChatFormatting.DARK_PURPLE )
			.create(),
		GameStage.named( GameStage.MASTER_ID )
			.format( ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD )
			.triggersByKilling( "minecraft:ender_dragon" )
			.message( "majruszsdifficulty.stages.master.started", ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD )
			.message( "majruszsdifficulty.undead_army.on_master", ChatFormatting.DARK_PURPLE )
			.create()
	) );

	static {
		Serializables.getStatic( GameStageConfig.class )
			.define( "is_per_player_difficulty_enabled", Reader.bool(), ()->IS_PER_PLAYER_DIFFICULTY_ENABLED, v->IS_PER_PLAYER_DIFFICULTY_ENABLED = v )
			.define( "list", Reader.list( Reader.custom( GameStage::new ) ), ()->GAME_STAGES, v->GAME_STAGES = GameStageConfig.validate( v ) );
	}

	private static List< GameStage > validate( List< GameStage > gameStages ) {
		boolean hasDefaultGameStages = gameStages.stream()
			.filter( gameStage->gameStage.is( GameStage.NORMAL_ID ) || gameStage.is( GameStage.EXPERT_ID ) || gameStage.is( GameStage.MASTER_ID ) )
			.count() == 3;
		if( !hasDefaultGameStages ) {
			throw new IllegalArgumentException( "Default game stages cannot be removed" );
		}

		for( GameStage gameStage : gameStages ) {
			long count = gameStages.stream().filter( stage->stage.equals( gameStage ) ).count();
			if( count > 1 ) {
				throw new IllegalArgumentException( "Found %d game stages with identical id (%s)".formatted( count, gameStage.getId() ) );
			}
		}

		GameStageConfig.keepOldReferencesValid( gameStages, GAME_STAGES );
		GameStageConfig.updateOrdinals( gameStages );

		return gameStages;
	}

	private static List< GameStage > keepOldReferencesValid( List< GameStage > newGameStages, List< GameStage > oldGameStages ) {
		for( int idx = 0; idx < newGameStages.size(); ++idx ) {
			GameStage newGameStage = newGameStages.get( idx );
			for( GameStage oldGameStage : oldGameStages ) {
				if( oldGameStage.is( newGameStage.getId() ) ) {
					newGameStage = oldGameStage; // to keep references valid
					break;
				}
			}
			newGameStages.set( idx, newGameStage );
		}

		return newGameStages;
	}

	private static List< GameStage > updateOrdinals( List< GameStage > gameStages ) {
		for( int idx = 0; idx < gameStages.size(); ++idx ) {
			gameStages.get( idx ).ordinal = idx;
		}

		return gameStages;
	}
}

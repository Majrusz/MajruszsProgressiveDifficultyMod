package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.data.Config;
import net.minecraft.ChatFormatting;

import java.util.List;

public class GameStageConfig {
	public static List< GameStage > GAME_STAGES = List.of(
		GameStage.named( GameStage.NORMAL_ID )
			.format( ChatFormatting.WHITE )
			.create(),
		GameStage.named( GameStage.EXPERT_ID )
			.format( ChatFormatting.RED, ChatFormatting.BOLD )
			.triggersIn( "{regex}.*" )
			.message( "majruszsdifficulty.stages.expert.started", ChatFormatting.RED, ChatFormatting.BOLD )
			.create(),
		GameStage.named( GameStage.MASTER_ID )
			.format( ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD )
			.triggersByKilling( "minecraft:ender_dragon" )
			.message( "majruszsdifficulty.stages.master.started", ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD )
			.create()
	);
	public static boolean IS_PER_PLAYER_DIFFICULTY_ENABLED = false;

	static {
		Serializables.getStatic( Config.class )
			.define( "game_stages", Reader.list( Reader.custom( GameStage::new ) ), ()->GAME_STAGES, v->GAME_STAGES = GameStageConfig.validate( v ) )
			.define( "is_per_player_difficulty_enabled", Reader.bool(), ()->IS_PER_PLAYER_DIFFICULTY_ENABLED, v->IS_PER_PLAYER_DIFFICULTY_ENABLED = v );
	}

	private static List< GameStage > validate( List< GameStage > gameStages ) {
		boolean hasDefaultGameStages = gameStages.stream()
			.filter( gameStage->gameStage.is( GameStage.NORMAL_ID ) || gameStage.is( GameStage.EXPERT_ID ) || gameStage.is( GameStage.MASTER_ID ) )
			.count() == 3;
		if( !hasDefaultGameStages ) {
			throw new IllegalArgumentException( "Default game stages cannot be removed" );
		}

		int idx = 0;
		for( GameStage gameStage : gameStages ) {
			gameStage.setOrdinal( idx++ );
			long count = gameStages.stream().filter( stage->stage.equals( gameStage ) ).count();
			if( count > 1 ) {
				throw new IllegalArgumentException( "Found %d game stages with identical id (%s)".formatted( count, gameStage.getId() ) );
			}
		}

		return gameStages;
	}
}

package com.majruszsdifficulty.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.data.Serializables;
import net.minecraft.ChatFormatting;

import java.util.List;

public class Config extends com.mlib.data.Config {
	public List< GameStage > gameStages = List.of(
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
	public boolean isPerPlayerDifficultyEnabled = false;
	public Bleeding bleeding = new Bleeding();

	public Config( String name ) {
		super( name );

		Serializables.get( Config.class )
			.defineCustomList( "game_stages", s->s.gameStages, ( s, v )->s.gameStages = Config.validate( v ), GameStage::new )
			.defineBoolean( "is_per_player_difficulty_enabled", s->s.isPerPlayerDifficultyEnabled, ( s, v )->s.isPerPlayerDifficultyEnabled = v )
			.defineCustom( "bleeding", s->s.bleeding, ( s, v )->s.bleeding = v, Bleeding::new );
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

	public static class Bleeding {
		public boolean isEnabled = true;

		static {
			Serializables.get( Bleeding.class )
				.defineBoolean( "is_enabled", s->s.isEnabled, ( s, v )->s.isEnabled = v );
		}
	}
}

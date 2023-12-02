package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.EXPERT_ID );

	static {
		OnEntityDamaged.listen( CreeperChainReaction::igniteCreeper )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.attacker instanceof Creeper );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_chain_reaction", CreeperChainReaction.class );

		Serializables.getStatic( CreeperChainReaction.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) );
	}

	private static void igniteCreeper( OnEntityDamaged data ) {
		( ( Creeper )data.target ).ignite();
	}
}

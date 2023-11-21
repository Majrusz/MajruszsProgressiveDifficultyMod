package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.disabledOn( GameStage.NORMAL_ID );

	static {
		OnEntityDamaged.listen( CreeperChainReaction::igniteCreeper )
			.addCondition( data->IS_ENABLED.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.attacker instanceof Creeper );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_chain_reaction", CreeperChainReaction.class );

		Serializables.getStatic( CreeperChainReaction.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) );
	}

	private static void igniteCreeper( OnEntityDamaged data ) {
		( ( Creeper )data.target ).ignite();
	}
}

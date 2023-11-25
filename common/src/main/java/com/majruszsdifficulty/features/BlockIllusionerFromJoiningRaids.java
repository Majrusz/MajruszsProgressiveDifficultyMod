package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.monster.Illusioner;

public class BlockIllusionerFromJoiningRaids {
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.NORMAL_ID );

	static {
		OnEntitySpawned.listen( BlockIllusionerFromJoiningRaids::blockJoiningRaids )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.entity instanceof Illusioner );

		Serializables.getStatic( Config.Features.class )
			.define( "block_illusioner_from_joining_raids", BlockIllusionerFromJoiningRaids.class );

		Serializables.getStatic( BlockIllusionerFromJoiningRaids.class )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) );
	}

	private static void blockJoiningRaids( OnEntitySpawned data ) {
		( ( Illusioner )data.entity ).setCanJoinRaid( false );
	}
}

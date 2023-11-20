package com.majruszsdifficulty.features;

import com.majruszlibrary.contexts.OnEntitySpawned;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.monster.Illusioner;

public class BlockIllusionerFromJoiningRaids {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.alwaysEnabled();

	static {
		OnEntitySpawned.listen( BlockIllusionerFromJoiningRaids::blockJoiningRaids )
			.addCondition( data->data.entity instanceof Illusioner );

		Serializables.getStatic( Config.Features.class )
			.define( "block_illusioner_from_joining_raids", BlockIllusionerFromJoiningRaids.class );

		Serializables.getStatic( BlockIllusionerFromJoiningRaids.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) );
	}

	private static void blockJoiningRaids( OnEntitySpawned data ) {
		( ( Illusioner )data.entity ).setCanJoinRaid( false );
	}
}

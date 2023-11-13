package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntitySpawned;
import com.mlib.data.Serializables;
import net.minecraft.world.entity.monster.Illusioner;

@AutoInstance
public class BlockIllusionerFromJoiningRaids {
	private GameStageValue< Boolean > isEnabled = GameStageValue.alwaysEnabled();

	public BlockIllusionerFromJoiningRaids() {
		OnEntitySpawned.listen( this::blockJoiningRaids )
			.addCondition( data->data.entity instanceof Illusioner );

		Serializables.get( Config.Features.class )
			.define( "block_illusioner_from_joining_raids", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
			} );
	}

	private void blockJoiningRaids( OnEntitySpawned data ) {
		( ( Illusioner )data.entity ).setCanJoinRaid( false );
	}
}

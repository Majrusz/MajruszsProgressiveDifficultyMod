package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.monster.Illusioner;

@AutoInstance
public class BlockIllusionerFromJoiningRaids extends GameModifier {
	public BlockIllusionerFromJoiningRaids() {
		super( Registries.Modifiers.DEFAULT );

		new OnSpawned.Context( this::blockJoiningRaids )
			.addCondition( data->data.target instanceof Illusioner )
			.insertTo( this );

		this.name( "BlockIllusionerFromJoiningRaids" ).comment( "Makes the Illusioner be unable to join any raid." );
	}

	private void blockJoiningRaids( OnSpawned.Data data ) {
		Illusioner illusioner = ( Illusioner )data.target;
		illusioner.setCanJoinRaid( false );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Illusioner;

public class BlockIllusionerFromJoiningRaids extends GameModifier {
	public BlockIllusionerFromJoiningRaids() {
		super( Registries.Modifiers.DEFAULT, "BlockIllusionerFromJoiningRaids", "Makes the Illusioner be unable to join any raid." );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::blockJoiningRaids );
		onSpawned.addCondition( data->data.target instanceof Illusioner );

		this.addContext( onSpawned );
	}

	private void blockJoiningRaids( OnSpawned.Data data ) {
		Illusioner illusioner = ( Illusioner )data.target;
		illusioner.setCanJoinRaid( false );
	}
}

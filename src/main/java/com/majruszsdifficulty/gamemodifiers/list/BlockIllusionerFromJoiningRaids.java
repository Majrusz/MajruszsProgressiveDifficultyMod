package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.monster.Illusioner;

@AutoInstance
public class BlockIllusionerFromJoiningRaids {
	public BlockIllusionerFromJoiningRaids() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "BlockIllusionerFromJoiningRaids" )
			.comment( "Makes the Illusioner be unable to join any raid." );

		OnSpawned.listen( this::blockJoiningRaids )
			.addCondition( Condition.predicate( data->data.target instanceof Illusioner ) )
			.insertTo( group );
	}

	private void blockJoiningRaids( OnSpawned.Data data ) {
		Illusioner illusioner = ( Illusioner )data.target;
		illusioner.setCanJoinRaid( false );
	}
}

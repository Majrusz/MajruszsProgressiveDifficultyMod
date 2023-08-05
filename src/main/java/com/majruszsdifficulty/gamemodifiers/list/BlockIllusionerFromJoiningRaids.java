package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnSpawned;
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

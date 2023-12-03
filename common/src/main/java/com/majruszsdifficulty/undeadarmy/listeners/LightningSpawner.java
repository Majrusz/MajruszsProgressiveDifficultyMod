package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.events.base.Priority;
import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightningSpawner {
	static {
		OnUndeadArmyStateChanged.listen( LightningSpawner::spawn )
			.priority( Priority.LOW )
			.addCondition( data->data.undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_PREPARING );
	}

	private static void spawn( OnUndeadArmyStateChanged data ) {
		List< UndeadArmy.MobInfo > mobsLeft = new ArrayList<>( data.undeadArmy.mobsLeft );
		Collections.shuffle( mobsLeft );
		int count = Math.min( mobsLeft.size(), 3 );
		for( int idx = 0; idx < count; ++idx ) {
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.getLevel() );
			if( lightningBolt != null ) {
				lightningBolt.moveTo( AnyPos.from( mobsLeft.get( idx ).position ).center().vec3() );
				lightningBolt.setVisualOnly( true );
				data.getLevel().addFreshEntity( lightningBolt );
			}
		}
	}
}

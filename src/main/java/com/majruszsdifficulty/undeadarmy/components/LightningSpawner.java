package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.MobInfo;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import com.mlib.math.AnyPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record LightningSpawner( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void onStateChanged() {
		if( this.undeadArmy.phase.state == Phase.State.WAVE_PREPARING ) {
			this.spawnLightningStrikes();
		}
	}

	private void spawnLightningStrikes() {
		List< MobInfo > mobsLeft = new ArrayList<>( this.undeadArmy.mobsLeft );
		Collections.shuffle( mobsLeft );
		for( int idx = 0; idx < 3 && idx < mobsLeft.size(); ++idx ) {
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( this.undeadArmy.level );
			if( lightningBolt != null ) {
				lightningBolt.moveTo( AnyPos.from( mobsLeft.get( idx ).position ).center().vec3() );
				lightningBolt.setVisualOnly( true );
				this.undeadArmy.level.addFreshEntity( lightningBolt );
			}
		}
	}
}

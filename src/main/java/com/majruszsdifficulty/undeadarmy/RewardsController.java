package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.entities.EntityHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ItemStack;

record RewardsController( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void onWaveFinished() {
		this.giveExperienceReward();
		if( this.undeadArmy.isLastWave() ) {
			this.giveTreasureReward();
		}
	}

	private void giveExperienceReward() {
		Config.WaveDef waveDef = this.undeadArmy.config.getWave( this.undeadArmy.currentWave );
		this.undeadArmy.participants.forEach( participant->{
			for( int i = 0; i < waveDef.experience / 4; ++i ) {
				EntityHelper.spawnExperience( this.undeadArmy.level, participant.position(), 4 );
			}
		} );
	}

	private void giveTreasureReward() {
		this.undeadArmy.participants.forEach( participant->{
			ItemHelper.giveItemStackToPlayer( new ItemStack( Registries.UNDEAD_ARMY_TREASURE_BAG.get() ), participant, this.undeadArmy.level );
		} );
	}
}

package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Updates wave progress after undead mob was killed. */
@Mod.EventBusSubscriber
public class UpdateWaveProgress {
	@SubscribeEvent
	public static void onUndeadKill( LivingDeathEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		UndeadArmyManagerOld undeadArmyManager = Registries.UNDEAD_ARMY_MANAGER;
		if( undeadArmyManager != null && undeadArmyManager.doesEntityBelongToUndeadArmy( entity ) ) {
			UndeadArmy undeadArmy = undeadArmyManager.findNearestUndeadArmy( entity.blockPosition() );
			if( undeadArmy != null )
				undeadArmy.increaseUndeadCounter();
		}
	}
}

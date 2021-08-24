package com.majruszs_difficulty.features.undead_army;

import com.majruszs_difficulty.RegistryHandler;
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
		UndeadArmyManager undeadArmyManager = RegistryHandler.UNDEAD_ARMY_MANAGER;
		if( undeadArmyManager != null && undeadArmyManager.doesEntityBelongToUndeadArmy( entity ) ) {
			UndeadArmy undeadArmy = undeadArmyManager.findNearestUndeadArmy( entity.blockPosition() );
			if( undeadArmy != null )
				undeadArmy.increaseUndeadCounter();
		}
	}
}

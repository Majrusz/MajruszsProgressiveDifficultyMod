package com.majruszsdifficulty.undeadarmy;

import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Freezes water nearby Undead Army units. */
@Mod.EventBusSubscriber
public class FreezeWater {
	@SubscribeEvent
	public static void freezeNearby( LivingEvent.LivingUpdateEvent event ) {
		if( isEntityValid( event.getEntityLiving() ) )
			LevelHelper.freezeWater( event.getEntityLiving(), 4.0, 30, 60, false );
	}

	/** Checks whether entity belongs to Undead Army. */
	protected static boolean isEntityValid( LivingEntity entity ) {
		return entity instanceof Monster && UndeadArmyOld.doesEntityBelongToUndeadArmy( entity ) && !entity.level.isClientSide;
	}
}

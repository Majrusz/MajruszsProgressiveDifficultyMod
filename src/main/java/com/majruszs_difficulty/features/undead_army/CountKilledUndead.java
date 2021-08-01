package com.majruszs_difficulty.features.undead_army;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.RegistryHandler;
import com.mlib.damage.DamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Counting killed undead and starting the Undead Army if all conditions were met. */
@Mod.EventBusSubscriber
public class CountKilledUndead {
	@SubscribeEvent
	public static void onUndeadKill( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof Monster ) )
			return;

		Monster monster = ( Monster )event.getEntityLiving();
		if( monster.getMobType() != MobType.UNDEAD )
			return;

		if( monster.level instanceof ServerLevel && RegistryHandler.UNDEAD_ARMY_MANAGER != null && RegistryHandler.UNDEAD_ARMY_MANAGER.doesEntityBelongToUndeadArmy(
			monster ) && updateUndeadArmyKillCounter( monster.position() ) )
			return;

		Player player = DamageHelper.getPlayerFromDamageSource( event.getSource() );
		if( player == null )
			return;

		increaseKill( player );
		spawnArmyIfPossible( player );
	}

	/** Returns amount of undead killed by the player. */
	public static int getKills( Player player ) {
		return player.getPersistentData()
			.getInt( UndeadArmyKeys.KILLED );
	}

	/** Increases player's kill counter by 1. */
	protected static void increaseKill( Player player ) {
		CompoundTag nbt = player.getPersistentData();
		nbt.putInt( UndeadArmyKeys.KILLED, getKills( player ) + 1 );

		player.save( nbt );
	}

	/** Spawns Undead Army at player's position if player met all requirements. */
	private static void spawnArmyIfPossible( Player player ) {
		CompoundTag nbt = player.getPersistentData();
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		if( nbt.getInt( UndeadArmyKeys.KILLED ) >= config.getRequiredKills() && player.level instanceof ServerLevel )
			if( RegistryHandler.UNDEAD_ARMY_MANAGER != null && RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player ) )
				nbt.putInt( UndeadArmyKeys.KILLED, 0 );
	}

	/** Updates kill counter of the Undead Army at given position. */
	private static boolean updateUndeadArmyKillCounter( Vec3 position ) {
		if( RegistryHandler.UNDEAD_ARMY_MANAGER == null )
			return false;

		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			undeadArmy.increaseUndeadCounter();
			return true;
		}

		return false;
	}
}

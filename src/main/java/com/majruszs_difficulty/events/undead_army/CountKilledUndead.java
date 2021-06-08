package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.RegistryHandler;
import com.mlib.damage.DamageHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Counting killed undead and starting the Undead Army if all conditions were met. */
@Mod.EventBusSubscriber
public class CountKilledUndead {
	@SubscribeEvent
	public static void onUndeadKill( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof MonsterEntity ) )
			return;

		MonsterEntity monster = ( MonsterEntity )event.getEntityLiving();
		if( monster.getCreatureAttribute() != CreatureAttribute.UNDEAD )
			return;

		if( monster.isServerWorld() && RegistryHandler.UNDEAD_ARMY_MANAGER.doesEntityBelongToUndeadArmy( monster ) && updateUndeadArmyKillCounter(
			monster.getPositionVec() ) )
			return;

		PlayerEntity player = DamageHelper.getPlayerFromDamageSource( event.getSource() );
		if( player == null )
			return;

		increaseKill( player );
		spawnArmyIfPossible( player );
	}

	/** Returns amount of undead killed by the player. */
	public static int getKills( PlayerEntity player ) {
		return player.getPersistentData()
			.getInt( UndeadArmyKeys.KILLED );
	}

	/** Increases player's kill counter by 1. */
	protected static void increaseKill( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();
		nbt.putInt( UndeadArmyKeys.KILLED, getKills( player ) + 1 );

		player.writeAdditional( nbt );
	}

	/** Spawns Undead Army at player's position if player met all requirements. */
	private static void spawnArmyIfPossible( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		if( nbt.getInt( UndeadArmyKeys.KILLED ) >= config.getRequiredKills() && player.isServerWorld() )
			if( RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player ) )
				nbt.putInt( UndeadArmyKeys.KILLED, 0 );
	}

	/** Updates kill counter of the Undead Army at given position. */
	private static boolean updateUndeadArmyKillCounter( Vector3d position ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			undeadArmy.increaseUndeadCounter();
			return true;
		}

		return false;
	}
}

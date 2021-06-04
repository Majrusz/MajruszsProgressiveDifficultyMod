package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Counting killed undead and starting Undead Army if all conditions were met. */
@Mod.EventBusSubscriber
public class CountKilledUndead {
	public static final String NBT_TAG = "UndeadKills";

	@SubscribeEvent
	public static void onUndeadKill( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof MonsterEntity ) )
			return;

		MonsterEntity monster = ( MonsterEntity )event.getEntityLiving();
		if( monster.getCreatureAttribute() != CreatureAttribute.UNDEAD )
			return;

		if( monster.isServerWorld() && RegistryHandler.UNDEAD_ARMY_MANAGER.doesEntityBelongToUndeadArmy( monster ) )
			if( updateUndeadArmy( monster.getPositionVec() ) )
				return;

		DamageSource damageSource = event.getSource();
		if( !( damageSource.getTrueSource() instanceof PlayerEntity ) )
			return;

		PlayerEntity player = ( PlayerEntity )damageSource.getTrueSource();
		increaseKill( player );
		spawnArmyIfPossible( player );
	}

	/** Returns current undead kill counter for given player. */
	public static int getKills( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();
		return nbt.getInt( NBT_TAG );
	}

	/** Updates player's kill counter. */
	protected static void increaseKill( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();
		nbt.putInt( NBT_TAG, getKills( player ) + 1 );

		player.writeAdditional( nbt );
	}

	/** Spawns Undead Army at player's position if player met all requirements. */
	private static void spawnArmyIfPossible( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();

		if( nbt.getInt( NBT_TAG ) >= Instances.UNDEAD_ARMY_CONFIG.getRequiredKills() && player.world instanceof ServerWorld )
			if( RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player ) )
				nbt.putInt( NBT_TAG, 0 );
	}

	private static boolean updateUndeadArmy( Vector3d position ) {
		UndeadArmy undeadArmy = RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( position ) );
		if( undeadArmy != null ) {
			undeadArmy.onUndeadKill();
			return true;
		}

		return false;
	}
}

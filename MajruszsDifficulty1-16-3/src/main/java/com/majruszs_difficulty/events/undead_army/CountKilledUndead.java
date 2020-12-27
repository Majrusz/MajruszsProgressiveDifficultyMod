package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CountKilledUndead {
	public static final String NBT_TAG = "UndeadKills";
	public static int killsToActiveArmy = 5;

	@SubscribeEvent
	public static void onUndeadKill( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof MonsterEntity ) )
			return;

		MonsterEntity monster = ( MonsterEntity )event.getEntityLiving();
		if( monster.getCreatureAttribute() != CreatureAttribute.UNDEAD )
			return;

		DamageSource damageSource = event.getSource();
		if( !( damageSource.getTrueSource() instanceof PlayerEntity ) )
			return;

		PlayerEntity player = ( PlayerEntity )damageSource.getTrueSource();

		increaseKill( player );
		spawnArmyIfPossible( player );
	}

	public static int getKills( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();

		return ( nbt.contains( NBT_TAG ) ? nbt.getInt( NBT_TAG ) : 0 );
	}

	protected static void increaseKill( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();
		nbt.putInt( NBT_TAG, getKills( player ) + 1 );

		player.writeAdditional( nbt );
	}

	private static void spawnArmyIfPossible( PlayerEntity player ) {
		CompoundNBT nbt = player.getPersistentData();

		if( nbt.getInt( NBT_TAG ) < killsToActiveArmy )
			return;

		if( player.world instanceof ServerWorld )
			if( RegistryHandler.undeadArmyManager.spawn( player, ( ServerWorld )player.world ) )
				nbt.putInt( NBT_TAG, 0 );
	}
}

package com.majruszs_difficulty.events.on_death;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.mlib.MajruszLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/** Whenever player dies from Zombie or Bleeding there will be Zombie spawned. */
public class SpawnPlayerZombieOnDeath extends OnDeathBase {
	private static final String CONFIG_NAME = "PlayerZombie";
	private static final String CONFIG_COMMENT = "Spawns special Zombie whenever player dies from Bleeding or Zombie.";

	public SpawnPlayerZombieOnDeath() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.EXPERT, false );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event ) {
		MajruszLibrary.LOGGER.info( "OOF!" );

		ServerWorld world = ( ServerWorld )target.world;
		PlayerEntity player = ( PlayerEntity )target;
		Entity entity = EntityType.ZOMBIE.spawn( world, null, null, target.getPosition(), SpawnReason.EVENT, true, true );
		if( entity == null )
			return;

		ItemStack playerSkull = getHead( player );

		ZombieEntity zombie = ( ZombieEntity )entity;
		zombie.setItemStackToSlot( EquipmentSlotType.HEAD, playerSkull );
		zombie.setDropChance( EquipmentSlotType.HEAD, 0.0f );
		zombie.setCustomName( player.getName() );
		zombie.setCanPickUpLoot( true );
		zombie.enablePersistence();
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean hasPlayerDied = target instanceof PlayerEntity;
		boolean isSourceZombieOrBleeding = attacker instanceof ZombieEntity || damageSource.damageType.equals( Instances.BLEEDING.getName() );

		return super.shouldBeExecuted( attacker, target, damageSource ) && hasPlayerDied && isSourceZombieOrBleeding;
	}

	/** Returns player's head item stack. */
	private static ItemStack getHead( PlayerEntity player ) {
		ItemStack playerSkull = new ItemStack( Items.PLAYER_HEAD, 1 );

		CompoundNBT nbt = playerSkull.getOrCreateTag();
		nbt.putString( "SkullOwner", player.getScoreboardName() );
		playerSkull.setTag( nbt );

		return playerSkull;
	}
}

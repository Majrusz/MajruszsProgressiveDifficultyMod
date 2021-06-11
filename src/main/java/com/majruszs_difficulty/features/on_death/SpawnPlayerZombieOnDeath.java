package com.majruszs_difficulty.features.on_death;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
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
	protected final DoubleConfig playerHeadChance;
	protected final DoubleConfig playerHeadDropChance;

	public SpawnPlayerZombieOnDeath() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.EXPERT, false );

		String headComment = "Chance for spawned Zombie to have a player's head.";
		this.playerHeadChance = new DoubleConfig( "head_chance", headComment, false, 1.0, 0.0, 1.0 );

		String chanceComment = "Chance for special Zombie to drop a player's head on death. (if it has one)";
		this.playerHeadDropChance = new DoubleConfig( "head_drop_chance", chanceComment, false, 0.01, 0.0, 1.0 );

		this.featureGroup.addConfigs( this.playerHeadChance, this.playerHeadDropChance );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event ) {
		ServerWorld world = ( ServerWorld )target.world;
		PlayerEntity player = ( PlayerEntity )target;
		EntityType< ? extends ZombieEntity > zombieType = getZombieType( attacker );
		Entity entity = zombieType.spawn( world, null, null, player.getPosition(), SpawnReason.EVENT, true, true );
		if( entity == null )
			return;

		ZombieEntity zombie = ( ZombieEntity )entity;

		if( Random.tryChance( this.playerHeadChance.get() ) ) {
			ItemStack playerSkull = getHead( player );
			zombie.setItemStackToSlot( EquipmentSlotType.HEAD, playerSkull );
			zombie.setDropChance( EquipmentSlotType.HEAD, ( float )( double )this.playerHeadDropChance.get() );
		}

		zombie.setCustomName( player.getName() );
		zombie.setCanPickUpLoot( true );
		zombie.enablePersistence();
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean hasPlayerDied = target instanceof PlayerEntity;
		boolean hasDiedFromBleeding = damageSource.damageType.equals( Instances.BLEEDING_SOURCE.damageType );
		boolean hasDiedFromZombie = attacker instanceof ZombieEntity;

		return super.shouldBeExecuted( attacker, target, damageSource ) && hasPlayerDied && ( hasDiedFromZombie || hasDiedFromBleeding );
	}

	/** Returns player's head item stack. */
	private ItemStack getHead( PlayerEntity player ) {
		ItemStack playerSkull = new ItemStack( Items.PLAYER_HEAD, 1 );

		CompoundNBT nbt = playerSkull.getOrCreateTag();
		nbt.putString( "SkullOwner", player.getScoreboardName() );
		playerSkull.setTag( nbt );

		return playerSkull;
	}

	/** Returns Zombie type depending on entity that killed the player. */
	private EntityType< ? extends ZombieEntity > getZombieType( @Nullable LivingEntity attacker ) {
		if( attacker instanceof ZombifiedPiglinEntity )
			return EntityType.ZOMBIFIED_PIGLIN;
		else if( attacker instanceof HuskEntity )
			return EntityType.HUSK;
		else
			return EntityType.ZOMBIE;
	}
}

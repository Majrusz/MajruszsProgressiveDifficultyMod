package com.majruszsdifficulty.features.on_death;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/** Whenever player dies from Zombie or Bleeding there will be Zombie spawned. */
public class SpawnPlayerZombieOnDeath extends OnDeathBase {
	private static final String CONFIG_NAME = "PlayerZombie";
	private static final String CONFIG_COMMENT = "Spawns special Zombie whenever player dies from Bleeding or Zombie.";
	protected final DoubleConfig playerHeadChance;
	protected final DoubleConfig playerHeadDropChance;

	public SpawnPlayerZombieOnDeath() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameStage.Stage.EXPERT, false );

		String headComment = "Chance for spawned Zombie to have a player's head.";
		this.playerHeadChance = new DoubleConfig( "head_chance", headComment, false, 1.0, 0.0, 1.0 );

		String chanceComment = "Chance for special Zombie to drop a player's head on death. (if it has one)";
		this.playerHeadDropChance = new DoubleConfig( "head_drop_chance", chanceComment, false, 0.01, 0.0, 1.0 );

		this.featureGroup.addConfigs( this.playerHeadChance, this.playerHeadDropChance );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event ) {
		ServerLevel world = ( ServerLevel )target.level;
		Player player = ( Player )target;
		EntityType< ? extends Zombie > zombieType = getZombieType( attacker );
		Entity entity = zombieType.spawn( world, null, null, player.blockPosition(), MobSpawnType.EVENT, true, true );
		if( entity == null )
			return;

		Zombie zombie = ( Zombie )entity;

		if( Random.tryChance( this.playerHeadChance.get() ) ) {
			ItemStack playerSkull = getHead( player );
			zombie.setItemSlot( EquipmentSlot.HEAD, playerSkull );
			zombie.setDropChance( EquipmentSlot.HEAD, ( float )( double )this.playerHeadDropChance.get() );
		}

		zombie.setCustomName( player.getName() );
		zombie.setCanPickUpLoot( false );
		zombie.setPersistenceRequired();
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean hasPlayerDied = target instanceof Player;
		boolean hasDiedFromBleeding = BleedingEffect.isBleedingSource( damageSource );
		boolean hasDiedFromZombie = attacker instanceof Zombie;

		return super.shouldBeExecuted( attacker, target, damageSource ) && hasPlayerDied && ( hasDiedFromZombie || hasDiedFromBleeding );
	}

	/** Returns player's head item stack. */
	private ItemStack getHead( Player player ) {
		ItemStack playerSkull = new ItemStack( Items.PLAYER_HEAD, 1 );

		CompoundTag nbt = playerSkull.getOrCreateTag();
		nbt.putString( "SkullOwner", player.getScoreboardName() );
		playerSkull.setTag( nbt );

		return playerSkull;
	}

	/** Returns Zombie type depending on entity that killed the player. */
	private EntityType< ? extends Zombie > getZombieType( @Nullable LivingEntity attacker ) {
		if( attacker instanceof ZombifiedPiglin )
			return EntityType.ZOMBIFIED_PIGLIN;
		else if( attacker instanceof Husk )
			return EntityType.HUSK;
		else
			return EntityType.ZOMBIE;
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.data.OnDeathData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class SpawnPlayerZombie extends DifficultyModifier {
	final DoubleConfig headChance = new DoubleConfig( "head_chance", "Chance for a zombie to have player's head.", false, 1.0, 0.0, 1.0 );
	final DoubleConfig headDropChance = new DoubleConfig( "head_drop_chance", "Chance for a zombie to drop player's head.", false, 0.1, 0.0, 1.0 );

	public SpawnPlayerZombie() {
		super( DifficultyModifier.DEFAULT, "SpawnPlayerZombie", "If the player dies from a zombie or bleeding, then a zombie with player's name spawns in the same place." );

		OnDeathContext onDeath = new OnDeathContext( this::spawnZombie );
		onDeath.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance( 1.0, false ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.level != null )
			.addCondition( data->data.target instanceof Player )
			.addCondition( data->data.target.hasEffect( Registries.BLEEDING.get() ) || data.attacker instanceof Zombie )
			.addConfigs( this.headChance, this.headDropChance );

		this.addContext( onDeath );
	}

	private void spawnZombie( OnDeathData data ) {
		assert data.level != null;
		Player player = ( Player )data.target;
		EntityType< ? extends Zombie > zombieType = getZombieType( data.attacker );
		Zombie zombie = ( Zombie )zombieType.spawn( data.level, null, null, player.blockPosition(), MobSpawnType.EVENT, true, true );
		if( zombie == null )
			return;

		if( Random.tryChance( this.headChance.get() ) ) {
			ItemStack playerSkull = getHead( player );
			zombie.setItemSlot( EquipmentSlot.HEAD, playerSkull );
			zombie.setDropChance( EquipmentSlot.HEAD, this.headDropChance.get().floatValue() );
		}

		zombie.setCustomName( player.getName() );
		zombie.setCanPickUpLoot( false );
		zombie.setPersistenceRequired();
	}

	private static ItemStack getHead( Player player ) {
		ItemStack playerSkull = new ItemStack( Items.PLAYER_HEAD, 1 );

		CompoundTag nbt = playerSkull.getOrCreateTag();
		nbt.putString( "SkullOwner", player.getScoreboardName() );
		playerSkull.setTag( nbt );

		return playerSkull;
	}

	private static EntityType< ? extends Zombie > getZombieType( @Nullable LivingEntity attacker ) {
		if( attacker instanceof ZombifiedPiglin )
			return EntityType.ZOMBIFIED_PIGLIN;
		else if( attacker instanceof Husk )
			return EntityType.HUSK;

		return EntityType.ZOMBIE;
	}
}

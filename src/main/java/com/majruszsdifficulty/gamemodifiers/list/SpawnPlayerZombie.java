package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.math.Range;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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

@AutoInstance
public class SpawnPlayerZombie {
	final DoubleConfig headChance = new DoubleConfig( 1.0, Range.CHANCE );
	final DoubleConfig headDropChance = new DoubleConfig( 0.1, Range.CHANCE );

	public SpawnPlayerZombie() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpawnPlayerZombie" )
			.comment( "If the player dies from a zombie or bleeding, then a zombie with player's name spawns in the same place." );

		OnDeath.listen( this::spawnZombie )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 1.0, false ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Player ) )
			.addCondition( Condition.predicate( data->data.target.hasEffect( Registries.BLEEDING.get() ) || data.attacker instanceof Zombie ) )
			.addConfig( this.headChance.name( "head_chance" ).comment( "Chance for a zombie to have player's head." ) )
			.addConfig( this.headDropChance.name( "head_drop_chance" ).comment( "Chance for a zombie to drop player's head." ) )
			.insertTo( group );

		OnDeath.listen( this::giveAdvancement )
			.addCondition( Condition.predicate( data->data.target instanceof Zombie ) )
			.addCondition( Condition.predicate( data->data.attacker instanceof ServerPlayer ) )
			.addCondition( Condition.predicate( data->data.target.getName().equals( data.attacker.getName() ) ) )
			.insertTo( group );
	}

	private void spawnZombie( OnDeath.Data data ) {
		Player player = ( Player )data.target;
		EntityType< ? extends Zombie > zombieType = getZombieType( data.attacker );
		Zombie zombie = zombieType.spawn( data.getServerLevel(), ( CompoundTag )null, null, null, player.blockPosition(), MobSpawnType.EVENT, true, true );
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

	private void giveAdvancement( OnDeath.Data data ) {
		Registries.BASIC_TRIGGER.trigger( ( ServerPlayer )data.attacker, "kill_yourself" );
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

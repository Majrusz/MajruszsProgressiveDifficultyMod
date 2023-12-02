package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SpawnPlayerZombie {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.EXPERT_ID );
	private static float CHANCE = 1.0f;
	private static boolean IS_SCALED_BY_CRD = false;
	private static float HEAD_CHANCE = 1.0f;
	private static float HEAD_DROP_CHANCE = 0.1f;

	static {
		OnEntityDied.listen( SpawnPlayerZombie::spawn )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.target instanceof Player )
			.addCondition( data->data.target.hasEffect( MajruszsDifficulty.BLEEDING_EFFECT.get() ) || data.attacker instanceof Zombie );

		OnEntityDied.listen( SpawnPlayerZombie::giveAdvancement )
			.addCondition( data->data.target instanceof Zombie )
			.addCondition( data->data.attacker instanceof ServerPlayer )
			.addCondition( data->data.target.getName().equals( data.attacker.getName() ) );

		Serializables.getStatic( Config.Features.class )
			.define( "spawn_player_zombie", SpawnPlayerZombie.class );

		Serializables.getStatic( SpawnPlayerZombie.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v )
			.define( "head_chance", Reader.number(), ()->HEAD_CHANCE, v->HEAD_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "head_drop_chance", Reader.number(), ()->HEAD_DROP_CHANCE, v->HEAD_DROP_CHANCE = Range.CHANCE.clamp( v ) );
	}

	private static void spawn( OnEntityDied data ) {
		Player player = ( Player )data.target;
		EntityType< ? > entityType = data.attacker instanceof Zombie attacker ? attacker.getType() : EntityType.ZOMBIE;
		Entity entity = EntityHelper.createSpawner( ()->entityType, data.getLevel() )
			.position( AnyPos.from( player.position() ).center().vec3() )
			.mobSpawnType( MobSpawnType.EVENT )
			.spawn();
		if( !( entity instanceof Zombie zombie ) ) {
			return;
		}

		if( Random.check( HEAD_CHANCE ) ) {
			ItemStack playerSkull = new ItemStack( Items.PLAYER_HEAD );
			playerSkull.getOrCreateTag().putString( "SkullOwner", player.getScoreboardName() );
			zombie.setItemSlot( EquipmentSlot.HEAD, playerSkull );
			zombie.setDropChance( EquipmentSlot.HEAD, HEAD_DROP_CHANCE );
		}

		zombie.setCustomName( player.getName() );
		zombie.setCanPickUpLoot( false );
		zombie.setPersistenceRequired();
	}

	private static void giveAdvancement( OnEntityDied data ) {
		MajruszsDifficulty.HELPER.triggerAchievement( ( ServerPlayer )data.attacker, "kill_yourself" );
	}
}

package com.majruszsdifficulty.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.blocks.BlockHelper;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.contexts.OnLootTableCustomLoad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CursedArmorEntity extends Monster {
	public static Supplier< EntityType< CursedArmorEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CursedArmorEntity::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 3.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.75 )
			.build();
	}

	public CursedArmorEntity( EntityType< ? extends CursedArmorEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 4 );
	}

	@AutoInstance
	public static class Spawn extends GameModifier {
		static final String MAIN_TAG = "cursed_armor";
		static final String LOOT_TABLE_TAG = "loot";
		static final String CHANCE_TAG = "chance";
		static final Map< ResourceLocation, Data > DATA_MAP = new HashMap<>();

		public Spawn() {
			super( Registries.Modifiers.DEFAULT, "", "" );

			OnLoot.Context onLoot = new OnLoot.Context( this::spawnCursedArmor );
			onLoot.addCondition( new Condition.IsServer() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->BlockHelper.getBlockEntity( data.level, data.origin ) instanceof ChestBlockEntity )
				.addCondition( data->data.entity instanceof ServerPlayer )
				.addCondition( this::hasLootDefined );

			OnLootTableCustomLoad.Context onLootTableLoad = new OnLootTableCustomLoad.Context( this::loadCursedArmorLoot );
			onLootTableLoad.addCondition( data->data.jsonObject.has( MAIN_TAG ) );
		}

		private void spawnCursedArmor( OnLoot.Data data ) {
			Vec3 position = data.origin.add( 0.0, 0.5, 0.0 );
			CursedArmorEntity cursedArmor = EntityHelper.spawn( Registries.CURSED_ARMOR, data.level, position );
			if( cursedArmor == null )
				return;

			LootTable lootTable = DATA_MAP.get( data.context.getQueriedLootTableId() ).lootTable;
			LootContext lootContext = new LootContext.Builder( data.level )
				.withParameter( LootContextParams.ORIGIN, data.origin )
				.withParameter( LootContextParams.THIS_ENTITY, cursedArmor )
				.create( LootContextParamSets.GIFT );

			lootTable.getRandomItems( lootContext )
				.forEach( cursedArmor::equipItemIfPossible );
		}

		private void loadCursedArmorLoot( OnLootTableCustomLoad.Data data ) {
			JsonObject object = data.jsonObject.get( MAIN_TAG ).getAsJsonObject();
			double chance = object.has( CHANCE_TAG ) ? object.get( CHANCE_TAG ).getAsDouble() : 1.0;
			JsonElement ids = object.get( LOOT_TABLE_TAG );
			if( ids.isJsonArray() ) {
				JsonArray array = ids.getAsJsonArray();
				array.forEach( id->DATA_MAP.put( new ResourceLocation( id.getAsString() ), new Data( data.table, chance ) ) );
			} else {
				DATA_MAP.put( new ResourceLocation( ids.getAsString() ), new Data( data.table, chance ) );
			}
		}

		private boolean hasLootDefined( OnLoot.Data data ) {
			ResourceLocation lootTableId = data.context.getQueriedLootTableId();

			return DATA_MAP.containsKey( lootTableId )
				&& Random.tryChance( DATA_MAP.get( lootTableId ).chance );
		}

		private record Data( LootTable lootTable, double chance ) {}
	}
}

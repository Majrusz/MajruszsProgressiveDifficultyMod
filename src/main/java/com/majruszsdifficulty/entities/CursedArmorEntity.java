package com.majruszsdifficulty.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.blocks.BlockHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.config.StringConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.*;
import com.mlib.math.VectorHelper;
import com.mlib.text.TextHelper;
import com.mlib.time.Anim;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class CursedArmorEntity extends Monster {
	public static final String GROUP_ID = "CursedArmor";
	public static final int ASSEMBLE_DURATION = Utility.secondsToTicks( 4.5 );
	private int assembleTicksLeft = 0;

	static {
		GameModifier.addNewGroup( Registries.Modifiers.MOBS, GROUP_ID, "CursedArmor", "" );
	}

	public static Supplier< EntityType< CursedArmorEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CursedArmorEntity::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.23 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 35.0 )
			.add( Attributes.ARMOR, 3.0 )
			.build();
	}

	public CursedArmorEntity( EntityType< ? extends CursedArmorEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 4 );
	}

	@Override
	public void tick() {
		super.tick();
		this.assembleTicksLeft = Math.max( this.assembleTicksLeft - 1, 0 );
		if( !this.isAssembling() && this.goalSelector.getAvailableGoals().isEmpty() ) {
			this.registerDefaultGoals();
		}
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn( ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type,
		@Nullable SpawnGroupData data, @Nullable CompoundTag compoundTag
	) {
		this.startAssembling();

		return super.finalizeSpawn( level, difficulty, type, data, compoundTag );
	}

	public void startAssembling() {
		this.assembleTicksLeft = ASSEMBLE_DURATION;
	}

	public boolean isAssembling() {
		return this.assembleTicksLeft > 0;
	}

	@Override
	protected void registerGoals() {}

	protected void registerDefaultGoals() {
		this.goalSelector.addGoal( 2, new MeleeAttackGoal( this, 1.0D, false ) );
		this.goalSelector.addGoal( 3, new WaterAvoidingRandomStrollGoal( this, 1.0D ) );
		this.goalSelector.addGoal( 4, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 4, new RandomLookAroundGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	@AutoInstance
	public static class Spawn extends GameModifier {
		static final String MAIN_TAG = "cursed_armor";
		static final String LOOT_TABLE_TAG = "loot";
		static final String CHANCE_TAG = "chance";
		static final Map< ResourceLocation, Data > DATA_MAP = new HashMap<>();
		final DoubleConfig dropChance = new DoubleConfig( "drop_chance", "Chance for each equipped item to drop when killed.", false, 0.1, 0.0, 1.0 );
		final StringConfig name = new StringConfig( "name", "", false, "Freshah" );

		public Spawn() {
			super( GROUP_ID, "", "" );

			OnLoot.Context onLoot = new OnLoot.Context( this::spawnCursedArmor );
			onLoot.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->BlockHelper.getBlockEntity( data.level, data.origin ) instanceof ChestBlockEntity )
				.addCondition( data->data.entity instanceof ServerPlayer )
				.addCondition( this::hasLootDefined )
				.addConfig( this.dropChance );

			OnLootTableCustomLoad.Context onLootTableLoad = new OnLootTableCustomLoad.Context( this::loadCursedArmorLoot );
			onLootTableLoad.addCondition( data->data.jsonObject.has( MAIN_TAG ) );

			OnEntityTick.Context onTick = new OnEntityTick.Context( this::spawnParticles );
			onTick.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 0.2, Dist.DEDICATED_SERVER ).setConfigurable( false ) )
				.addCondition( data->data.entity instanceof CursedArmorEntity );

			OnSpawned.Context onSpawned1 = new OnSpawned.Context( this::setCustomName, "CustomName", "Makes some Cursed Armors have a custom name." );
			onSpawned1.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Chance<>( 0.025, "chance", "" ) )
				.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
				.addCondition( data->data.target instanceof CursedArmorEntity )
				.addConfigs( this.name );

			OnSpawned.Context onSpawned2 = new OnSpawned.Context( this::giveRandomArmor );
			onSpawned2.addCondition( new Condition.IsServer<>() )
				.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
				.addCondition( data->data.target instanceof CursedArmorEntity );

			this.addContexts( onLoot, onLootTableLoad, onTick, onSpawned1, onSpawned2 );
		}

		private void spawnCursedArmor( OnLoot.Data data ) {
			CursedArmorEntity cursedArmor = EntityHelper.spawn( Registries.CURSED_ARMOR, data.level, this.getSpawnPosition( data ) );
			if( cursedArmor != null ) {
				this.equipSet( DATA_MAP.get( data.context.getQueriedLootTableId() ), cursedArmor, data.origin );
			}
		}

		private Vec3 getSpawnPosition( OnLoot.Data data ) {
			ServerLevel level = data.level;
			Vec3 origin = data.origin;
			Function< Float, Boolean > isAir = y->BlockHelper.getBlockState( level, origin.add( 0.0, y, 0.0 ) ).isAir();
			if( isAir.apply( 1.0f ) && isAir.apply( 2.0f ) ) {
				return origin.add( 0.0, 0.5, 0.0 );
			} else {
				Vec3i offset = BlockHelper.getBlockState( data.level, data.origin ).getValue( ChestBlock.FACING ).getNormal();
				return origin.add( offset.getX(), offset.getY(), offset.getZ() );
			}
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

			return DATA_MAP.containsKey( lootTableId ) && Random.tryChance( DATA_MAP.get( lootTableId ).chance );
		}

		private void spawnParticles( OnEntityTick.Data data ) {
			Entity entity = data.entity;
			Vec3 position = entity.position().add( 0.0, entity.getBbHeight() * 0.5, 0.0 );
			Vec3 offset = VectorHelper.multiply( new Vec3( entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth() ), 0.3 );
			ParticleHandler.ENCHANTED_GLYPH.spawn( data.level, position, 1, ()->offset, ()->0.5f );
		}

		private void setCustomName( OnSpawned.Data data ) {
			data.target.setCustomName( this.name.asLiteral() );
		}

		private void giveRandomArmor( OnSpawned.Data data ) {
			Anim.nextTick( ()->{
				CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.target;
				if( cursedArmor.getArmorCoverPercentage() > 0.0f )
					return;

				this.equipSet( Random.nextRandom( DATA_MAP ).getValue(), cursedArmor, cursedArmor.position() );
			} );
		}

		private void equipSet( Data data, CursedArmorEntity cursedArmor, Vec3 position ) {
			LootContext lootContext = new LootContext.Builder( ( ServerLevel )cursedArmor.level )
				.withParameter( LootContextParams.ORIGIN, position )
				.withParameter( LootContextParams.THIS_ENTITY, cursedArmor )
				.create( LootContextParamSets.GIFT );

			data.lootTable.getRandomItems( lootContext )
				.forEach( cursedArmor::equipItemIfPossible );

			Arrays.stream( EquipmentSlot.values() )
				.forEach( slot->cursedArmor.setDropChance( slot, this.dropChance.asFloat() ) );
		}

		private record Data( LootTable lootTable, double chance ) {}
	}

	@AutoInstance
	public static class TooltipUpdater {
		public TooltipUpdater() {
			new OnItemTooltip.Context( this::addSpawnInfo )
				.addCondition( data->data.itemStack.getItem().equals( Registries.CURSED_ARMOR_SPAWN_EGG.get() ) );
		}

		private void addSpawnInfo( OnItemTooltip.Data data ) {
			List< Component > components = data.tooltip;
			components.add( Component.translatable( "item.majruszsdifficulty.cursed_armor_spawn_egg.locations" )
				.withStyle( ChatFormatting.GRAY ) );

			Spawn.DATA_MAP.forEach( ( location, spawnData )->{
				String chance = TextHelper.percent( ( float )spawnData.chance );
				components.add( Component.literal( " - " )
					.append( Component.literal( location.toString() ) )
					.append( Component.literal( " " ) )
					.append( Component.literal( chance ).withStyle( ChatFormatting.DARK_GRAY ) )
					.withStyle( ChatFormatting.GRAY )
				);
			} );
		}
	}
}

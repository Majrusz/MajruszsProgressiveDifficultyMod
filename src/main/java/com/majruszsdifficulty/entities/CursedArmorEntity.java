package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.blocks.BlockHelper;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.StringConfig;
import com.mlib.data.JsonListener;
import com.mlib.data.SerializableList;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.*;
import com.mlib.loot.LootHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
import com.mlib.text.TextHelper;
import com.mlib.time.Time;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CursedArmorEntity extends Monster {
	public static final String GROUP_ID = Registries.getLocationString( "cursed_armor" );
	public static final int ASSEMBLE_DURATION = Utility.secondsToTicks( 2.5 );
	boolean areGoalsRegistered = false;
	SoundHandler assembleSound = null;
	int assembleTicksLeft = 0;

	static {
		ModConfigs.init( Registries.Groups.MOBS, GROUP_ID ).name( "CursedArmor" );
	}

	public static Supplier< EntityType< CursedArmorEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CursedArmorEntity::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.23 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 35.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.23 )
			.add( Attributes.ARMOR, 4.0 )
			.build();
	}

	public CursedArmorEntity( EntityType< ? extends CursedArmorEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 7 );
	}

	@Override
	public void tick() {
		super.tick();

		this.tryToPlaySfx();
		this.assembleTicksLeft = Math.max( this.assembleTicksLeft - 1, 0 );
		if( !this.areGoalsRegistered ) {
			this.registerGoals();
		}
	}

	public void startAssembling( float yRot ) {
		this.assembleTicksLeft = ASSEMBLE_DURATION;
		this.setYRot( yRot );
		this.setYHeadRot( yRot );
		this.setYBodyRot( yRot );
		if( this.level instanceof ServerLevel ) {
			Time.nextTick( ()->Registries.HELPER.sendMessage( PacketDistributor.DIMENSION.with( ()->this.level.dimension() ), new AssembleMessage( this, yRot ) ) );
		}
	}

	public void setAssembleSound( SoundEvent soundEvent ) {
		this.assembleSound = new SoundHandler( soundEvent, SoundSource.HOSTILE, SoundHandler.randomized( 2.0f ) );
	}

	public boolean isAssembling() {
		return this.assembleTicksLeft > 0;
	}

	public float getAssembleTime() {
		return ( float )Utility.ticksToSeconds( ASSEMBLE_DURATION - this.assembleTicksLeft );
	}

	@Override
	protected void registerGoals() {
		if( this.isAssembling() || this.tickCount <= ASSEMBLE_DURATION / 3 )
			return;

		this.goalSelector.addGoal( 2, new MeleeAttackGoal( this, 1.0D, false ) );
		this.goalSelector.addGoal( 3, new WaterAvoidingRandomStrollGoal( this, 1.0D ) );
		this.goalSelector.addGoal( 4, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 4, new RandomLookAroundGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
		this.areGoalsRegistered = true;
	}

	private void tryToPlaySfx() {
		if( !( this.level instanceof ServerLevel level ) )
			return;

		if( this.assembleTicksLeft == ASSEMBLE_DURATION ) {
			SoundHandler.ENCHANT.play( level, this.position() );
		} else if( this.assembleTicksLeft == ASSEMBLE_DURATION - 35 ) {
			this.assembleSound.play( level, this.position() );
		}
	}

	@AutoInstance
	public static class Spawn {
		final DoubleConfig dropChance = new DoubleConfig( 0.2, Range.CHANCE );
		final StringConfig name = new StringConfig( "Freshah" );
		final JsonListener.Holder< LocationsDef > locationsDef;

		public Spawn() {
			this.locationsDef = JsonListener.add( "custom", Registries.getLocation( "cursed_armor_locations" ), LocationsDef.class, LocationsDef::new )
				.syncWithClients( Registries.HELPER );

			ConfigGroup group = ModConfigs.registerSubgroup( GROUP_ID );

			OnLoot.listen( this::spawnCursedArmor )
				.addCondition( Condition.isServer() )
				.addCondition( OnLoot.hasOrigin() )
				.addCondition( Condition.predicate( data->BlockHelper.getBlockEntity( data.getLevel(), data.origin ) instanceof ChestBlockEntity ) )
				.addCondition( this.isLootDefined() )
				.addConfig( this.dropChance.name( "drop_chance" ).comment( "Chance for each equipped item to drop when killed." ) )
				.insertTo( group );

			OnSpawned.listen( this::setCustomName )
				.name( "CustomName" )
				.comment( "Makes some Cursed Armors have a custom name." )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.chance( 0.025 ) )
				.addCondition( OnSpawned.isNotLoadedFromDisk() )
				.addCondition( Condition.predicate( data->data.target instanceof CursedArmorEntity ) )
				.addConfigs( this.name.name( "name" ) )
				.insertTo( group );

			OnSpawned.listenSafe( this::giveRandomArmor )
				.addCondition( Condition.isServer() )
				.addCondition( OnSpawned.isNotLoadedFromDisk() )
				.addCondition( Condition.predicate( data->data.target instanceof CursedArmorEntity ) )
				.insertTo( group );

			OnSpawned.listen( this::startAssembling )
				.addCondition( OnSpawned.isNotLoadedFromDisk() )
				.addCondition( Condition.predicate( data->data.target instanceof CursedArmorEntity cursedArmor && !cursedArmor.isAssembling() ) )
				.insertTo( group );

			OnPreDamaged.listen( OnPreDamaged.CANCEL )
				.addCondition( Condition.predicate( data->data.target instanceof CursedArmorEntity cursedArmor && cursedArmor.isAssembling() ) )
				.insertTo( group );

			OnItemTooltip.listen( this::addSpawnInfo )
				.addCondition( Condition.predicate( data->data.itemStack.getItem().equals( Registries.CURSED_ARMOR_SPAWN_EGG.get() ) ) );
		}

		private void spawnCursedArmor( OnLoot.Data data ) {
			CursedArmorEntity cursedArmor = EntityHelper.createSpawner( Registries.CURSED_ARMOR, data.getLevel() )
				.position( this.getSpawnPosition( data ) )
				.spawn();
			if( cursedArmor != null ) {
				float yRot = BlockHelper.getBlockState( data.getLevel(), data.origin )
					.getValue( ChestBlock.FACING )
					.toYRot();

				cursedArmor.startAssembling( yRot );
				this.equipSet( this.locationsDef.get().find( data.context.getQueriedLootTableId() ).orElseThrow(), cursedArmor, data.origin );
				if( data.entity instanceof ServerPlayer player ) {
					Time.nextTick( player::closeContainer );
				}
			}
		}

		private Vec3 getSpawnPosition( OnLoot.Data data ) {
			ServerLevel level = data.getServerLevel();
			Vec3 origin = data.origin;
			Function< Float, Boolean > isAir = y->BlockHelper.getBlockState( level, origin.add( 0.0, y, 0.0 ) ).isAir();
			if( isAir.apply( 1.0f ) && isAir.apply( 2.0f ) ) {
				return origin.add( 0.0, 0.5, 0.0 );
			} else {
				Vec3i offset = BlockHelper.getBlockState( level, data.origin ).getValue( ChestBlock.FACING ).getNormal();
				return origin.add( offset.getX(), offset.getY(), offset.getZ() );
			}
		}

		private Condition< OnLoot.Data > isLootDefined() {
			return Condition.predicate( data->{
				Optional< LocationDef > locationDef = this.locationsDef.get().find( data.context.getQueriedLootTableId() );

				return locationDef.isPresent() && Random.tryChance( locationDef.get().chance );
			} );
		}

		private void setCustomName( OnSpawned.Data data ) {
			data.target.setCustomName( Component.literal( this.name.get() ) );
		}

		private void giveRandomArmor( OnSpawned.Data data ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.target;
			if( cursedArmor.getArmorCoverPercentage() > 0.0f ) {
				return;
			}

			this.equipSet( this.locationsDef.get().getRandom(), cursedArmor, cursedArmor.position() );
		}

		private void equipSet( LocationDef locationDef, CursedArmorEntity cursedArmor, Vec3 position ) {
			LootContext lootContext = new LootContext.Builder( ( ServerLevel )cursedArmor.level )
				.withParameter( LootContextParams.ORIGIN, position )
				.withParameter( LootContextParams.THIS_ENTITY, cursedArmor )
				.create( LootContextParamSets.GIFT );

			LootHelper.getLootTable( locationDef.loot )
				.getRandomItems( lootContext )
				.forEach( cursedArmor::equipItemIfPossible );

			Arrays.stream( EquipmentSlot.values() )
				.forEach( slot->cursedArmor.setDropChance( slot, this.dropChance.asFloat() ) );

			cursedArmor.setAssembleSound( ForgeRegistries.SOUND_EVENTS.getValue( locationDef.sound ) );
		}

		private void startAssembling( OnSpawned.Data data ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.target;
			cursedArmor.startAssembling( 0.0f );
		}

		private void addSpawnInfo( OnItemTooltip.Data data ) {
			List< Component > components = data.tooltip;
			components.add( Component.translatable( "item.majruszsdifficulty.cursed_armor_spawn_egg.locations" )
				.withStyle( ChatFormatting.GRAY ) );

			this.locationsDef.get().locationDefs.forEach( locationDef->{
				String chance = TextHelper.percent( locationDef.chance );
				locationDef.chests.forEach( chestId->{
					components.add( Component.literal( " - " )
						.append( Component.literal( chestId.toString() ) )
						.append( Component.literal( " " ) )
						.append( Component.literal( chance ).withStyle( ChatFormatting.DARK_GRAY ) )
						.withStyle( ChatFormatting.GRAY )
					);
				} );
			} );
		}
	}

	@AutoInstance
	public static class Effects {
		public Effects() {
			this.createOnTick( this::spawnIdleParticles );

			this.createOnTick( this::spawnAssemblingParticles )
				.addCondition( Condition.predicate( data->data.entity instanceof CursedArmorEntity cursedArmor && cursedArmor.isAssembling() ) );
		}

		private Context< OnEntityTick.Data > createOnTick( Consumer< OnEntityTick.Data > consumer ) {
			return OnEntityTick.listen( consumer )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.< OnEntityTick.Data > cooldown( 0.2, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.predicate( data->data.entity instanceof CursedArmorEntity ) );
		}

		private void spawnIdleParticles( OnEntityTick.Data data ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.entity;

			this.spawnParticles( data, new Vec3( 0.0, cursedArmor.getBbHeight() * 0.5, 0.0 ), 0.3, 1 );
		}

		private void spawnAssemblingParticles( OnEntityTick.Data data ) {
			this.spawnParticles( data, new Vec3( 0.0, 0.0, 0.0 ), 0.6, 5 );
		}

		private void spawnParticles( OnEntityTick.Data data, Vec3 emitterOffset, double offsetMultiplier, int particlesCount ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.entity;
			Vec3 position = cursedArmor.position().add( emitterOffset );
			Vec3 offset = AnyPos.from( cursedArmor.getBbWidth(), cursedArmor.getBbHeight(), cursedArmor.getBbWidth() ).mul( offsetMultiplier ).vec3();
			ParticleHandler.ENCHANTED_GLYPH.spawn( data.getServerLevel(), position, particlesCount, ()->offset, ()->0.5f );
		}
	}

	public static class AssembleMessage extends SerializableStructure {
		int entityId;
		float yRot;

		public AssembleMessage() {
			this.defineInteger( "id", ()->this.entityId, x->this.entityId = x );
			this.defineFloat( "rot", ()->this.yRot, x->this.yRot = x );
		}

		public AssembleMessage( Entity entity, float yRot ) {
			this();

			this.entityId = entity.getId();
			this.yRot = yRot;
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Level level = Minecraft.getInstance().level;
			if( level != null && level.getEntity( this.entityId ) instanceof CursedArmorEntity cursedArmor ) {
				cursedArmor.startAssembling( this.yRot );
			}
		}
	}

	public static class LocationsDef extends SerializableList {
		List< LocationDef > locationDefs = new ArrayList<>();

		public LocationsDef() {
			this.defineCustom( ()->this.locationDefs, x->this.locationDefs = x, LocationDef::new );
		}

		public Optional< LocationDef > find( ResourceLocation chestId ) {
			return this.locationDefs.stream()
				.filter( locationDef -> locationDef.chests.stream().anyMatch( chestId::equals ) )
				.findFirst();
		}

		public LocationDef getRandom() {
			return Random.next( this.locationDefs );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Registries.HELPER.findInstance( Spawn.class ).ifPresent( instance->instance.locationsDef.onSync( this ) );
		}
	}

	public static class LocationDef extends SerializableStructure {
		ResourceLocation loot;
		List< ResourceLocation > chests;
		ResourceLocation sound = new ResourceLocation( "item.armor.equip_generic" );
		float chance = 0.0f;

		public LocationDef() {
			this.defineLocation( "loot", ()->this.loot, x->this.loot = x );
			this.defineLocation( "chests", ()->this.chests, x->this.chests = x );
			this.defineLocation( "sound", ()->this.sound, x->this.sound = x );
			this.defineFloat( "chance", ()->this.chance, x->this.chance = x );
		}
	}
}

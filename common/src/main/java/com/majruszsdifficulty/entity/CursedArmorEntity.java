package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.animations.Animations;
import com.mlib.animations.AnimationsDef;
import com.mlib.animations.IAnimableEntity;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.contexts.OnEntitySpawned;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Context;
import com.mlib.data.Serializables;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.emitter.SoundEmitter;
import com.mlib.entity.EntityHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.ItemHelper;
import com.mlib.item.LootHelper;
import com.mlib.level.BlockHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Random;
import com.mlib.math.Range;
import com.mlib.modhelper.LazyResource;
import com.mlib.text.TextHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CursedArmorEntity extends Monster implements IAnimableEntity {
	private static final LazyResource< AnimationsDef > ANIMATIONS = MajruszsDifficulty.HELPER.load( "cursed_armor_animation", AnimationsDef.class, PackType.SERVER_DATA );
	private final Animations animations = Animations.create();

	public static EntityType< CursedArmorEntity > createEntityType() {
		return EntityType.Builder.of( CursedArmorEntity::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier createAttributes() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.23 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 35.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.23 )
			.add( Attributes.ARMOR, 4.0 )
			.build();
	}

	public CursedArmorEntity( EntityType< ? extends Monster > entityType, Level level ) {
		super( entityType, level );
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public int getExperienceReward() {
		return 7;
	}

	@Override
	public void tick() {
		super.tick();

	}

	@Override
	public AnimationsDef getAnimationsDef() {
		return ANIMATIONS.get();
	}

	@Override
	public Animations getAnimations() {
		return this.animations;
	}

	public void assemble() {
		if( this.animations.isEmpty() ) {
			this.playAnimation( "assemble" )
				.addCallback( 27, ()->{
					if( this.getItemBySlot( EquipmentSlot.CHEST ).getItem() instanceof ArmorItem armorItem ) {
						SoundEmitter.of( armorItem.getEquipSound() )
							.source( SoundSource.HOSTILE )
							.position( this.position() )
							.emit( this.level() );
					}
				} );
		}
	}

	public void equip( LocationDef locationDef ) {
		LootHelper.getLootTable( locationDef.loot )
			.getRandomItems( LootHelper.toGiftParams( this ) )
			.forEach( itemStack->{
				if( itemStack.getItem() instanceof ShieldItem ) {
					this.setItemSlot( EquipmentSlot.OFFHAND, itemStack );
				} else {
					ItemHelper.equip( this, itemStack );
				}
			} );

		EquipmentSlots.ALL.forEach( slot->this.setDropChance( slot, Config.ITEM_DROP_CHANCE ) );
	}

	public boolean isAssembling() {
		return !this.animations.isEmpty();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new AssembleGoal( this ) );
		this.goalSelector.addGoal( 2, new MeleeAttackGoal( this, 1.0, false ) );
		this.goalSelector.addGoal( 3, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 4, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 4, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	public static class AssembleGoal extends Goal {
		private final CursedArmorEntity cursedArmor;

		public AssembleGoal( CursedArmorEntity cursedArmor ) {
			this.cursedArmor = cursedArmor;

			this.setFlags( EnumSet.of( Flag.MOVE, Flag.LOOK ) );
		}

		@Override
		public boolean canUse() {
			return this.cursedArmor.isAssembling();
		}
	}

	@AutoInstance
	public static class Spawn {
		public Spawn() {
			OnLootGenerated.listen( this::spawnCursedArmor )
				.addCondition( Condition.isLogicalServer() )
				.addCondition( data->data.getLevel() != null )
				.addCondition( data->data.origin != null )
				.addCondition( data->BlockHelper.getEntity( data.getLevel(), data.origin ) instanceof ChestBlockEntity )
				.addCondition( data->Random.check( Config.find( data.lootId ).map( def->def.chance ).orElse( 0.0f ) ) );

			OnEntitySpawned.listen( this::giveRandomArmor )
				.addCondition( Condition.isLogicalServer() )
				.addCondition( data->!data.isLoadedFromDisk )
				.addCondition( data->data.entity instanceof CursedArmorEntity );

			OnEntitySpawned.listen( this::setCustomName )
				.addCondition( Condition.isLogicalServer() )
				.addCondition( Condition.chance( ()->Config.NAME_CHANCE ) )
				.addCondition( data->!data.isLoadedFromDisk )
				.addCondition( data->data.entity instanceof CursedArmorEntity );

			OnEntityPreDamaged.listen( OnEntityPreDamaged::cancelDamage )
				.addCondition( data->data.target instanceof CursedArmorEntity cursedArmor && cursedArmor.isAssembling() );
		}

		private void spawnCursedArmor( OnLootGenerated data ) {
			TimeHelper.nextTick( delay->{
				CursedArmorEntity cursedArmor = EntityHelper.createSpawner( MajruszsDifficulty.CURSED_ARMOR, data.getLevel() )
					.position( this.getSpawnPosition( data ) )
					.beforeEvent( entity->{
						float yRot = BlockHelper.getState( data.getLevel(), data.origin ).getValue( ChestBlock.FACING ).toYRot();
						entity.setYRot( yRot );
						entity.setYHeadRot( yRot );
						entity.setYBodyRot( yRot );
					} )
					.spawn();
				if( cursedArmor != null ) {
					cursedArmor.assemble();
					cursedArmor.equip( Config.find( data.lootId ).orElseThrow() );
					if( data.entity instanceof ServerPlayer player ) {
						TimeHelper.nextTick( subdelay->player.closeContainer() );
					}
				}
			} );
		}

		private void giveRandomArmor( OnEntitySpawned data ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.entity;
			if( cursedArmor.getArmorCoverPercentage() == 0.0f ) {
				cursedArmor.assemble();
				cursedArmor.equip( Config.getRandomLocationDef() );
			}
		}

		private void setCustomName( OnEntitySpawned data ) {
			data.entity.setCustomName( TextHelper.literal( Random.next( Config.NAMES ) ) );
		}

		private Vec3 getSpawnPosition( OnLootGenerated data ) {
			ServerLevel level = data.getServerLevel();
			Function< Float, Boolean > isAir = y->BlockHelper.getState( level, data.origin.add( 0.0, y, 0.0 ) ).isAir();
			if( isAir.apply( 1.0f ) && isAir.apply( 2.0f ) ) {
				return data.origin.add( 0.0, 0.5, 0.0 );
			} else {
				Vec3i offset = BlockHelper.getState( level, data.origin ).getValue( ChestBlock.FACING ).getNormal();
				return data.origin.add( offset.getX(), offset.getY(), offset.getZ() );
			}
		}
	}

	@AutoInstance
	public static class Effects {
		public Effects() {
			this.createOnEntityTicked( this::spawnIdleParticles );

			this.createOnEntityTicked( this::spawnAssemblingParticles )
				.addCondition( data->data.entity instanceof CursedArmorEntity cursedArmor && cursedArmor.isAssembling() );
		}

		private Context< OnEntityTicked > createOnEntityTicked( Consumer< OnEntityTicked > consumer ) {
			return OnEntityTicked.listen( consumer )
				.addCondition( Condition.isLogicalServer() )
				.addCondition( Condition.cooldown( 0.2f ) )
				.addCondition( data->data.entity instanceof CursedArmorEntity );
		}

		private void spawnIdleParticles( OnEntityTicked data ) {
			this.spawnParticles( data, new Vec3( 0.0, data.entity.getBbHeight() * 0.5, 0.0 ), 0.3, 1 );
		}

		private void spawnAssemblingParticles( OnEntityTicked data ) {
			this.spawnParticles( data, new Vec3( 0.0, 0.0, 0.0 ), 0.6, 5 );
		}

		private void spawnParticles( OnEntityTicked data, Vec3 emitterOffset, double offsetMultiplier, int particlesCount ) {
			ParticleEmitter.of( ParticleTypes.ENCHANT )
				.position( data.entity.position().add( emitterOffset ) )
				.offset( ()->AnyPos.from( data.entity.getBbWidth(), data.entity.getBbHeight(), data.entity.getBbWidth() ).mul( offsetMultiplier ).vec3() )
				.speed( 0.5f )
				.count( particlesCount )
				.emit( data.getLevel() );
		}
	}

	public static class Config {
		public static float ITEM_DROP_CHANCE = 0.2f;
		public static float NAME_CHANCE = 0.025f;
		public static List< String > NAMES = List.of( "Freshah" );
		public static List< LocationDef > LOCATIONS = List.of(
			new LocationDef(
				MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_dungeon" ),
				List.of( new ResourceLocation( "chests/simple_dungeon" ) ),
				0.5f
			),
			new LocationDef(
				MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_stronghold" ),
				List.of(
					new ResourceLocation( "chests/stronghold_corridor" ),
					new ResourceLocation( "chests/stronghold_crossing" ),
					new ResourceLocation( "chests/stronghold_library" )
				),
				0.4f
			),
			new LocationDef(
				MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_portal" ),
				List.of( new ResourceLocation( "chests/ruined_portal" ) ),
				1.0f
			),
			new LocationDef(
				MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_nether" ),
				List.of(
					new ResourceLocation( "chests/bastion_bridge" ),
					new ResourceLocation( "chests/bastion_hoglin_stable" ),
					new ResourceLocation( "chests/bastion_other" ),
					new ResourceLocation( "chests/bastion_treasure" ),
					new ResourceLocation( "chests/nether_bridge" )
				),
				0.25f
			),
			new LocationDef(
				MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_end" ),
				List.of( new ResourceLocation( "chests/end_city_treasure" ) ),
				0.5f
			)
		);

		static {
			Serializables.get( com.majruszsdifficulty.data.Config.Mobs.class )
				.define( "cursed_armor", subconfig->{
					subconfig.defineFloat( "item_drop_chance", s->ITEM_DROP_CHANCE, ( s, v )->ITEM_DROP_CHANCE = Range.CHANCE.clamp( v ) );
					subconfig.defineFloat( "custom_name_chance", s->NAME_CHANCE, ( s, v )->NAME_CHANCE = Range.CHANCE.clamp( v ) );
					subconfig.defineStringList( "custom_names", s->NAMES, ( s, v )->NAMES = v );
					subconfig.defineCustomList( "locations", s->LOCATIONS, ( s, v )->LOCATIONS = v, LocationDef::new );
				} );
		}

		public static Optional< LocationDef > find( ResourceLocation chestId ) {
			return LOCATIONS.stream()
				.filter( locationDef->locationDef.chests.stream().anyMatch( chestId::equals ) )
				.findFirst();
		}

		public static LocationDef getRandomLocationDef() {
			return Random.next( LOCATIONS );
		}
	}

	public static class LocationDef {
		public ResourceLocation loot;
		public List< ResourceLocation > chests;
		public float chance;

		static {
			Serializables.get( LocationDef.class )
				.defineLocation( "loot", s->s.loot, ( s, v )->s.loot = v )
				.defineLocationList( "chests", s->s.chests, ( s, v )->s.chests = v )
				.defineFloat( "chance", s->s.chance, ( s, v )->s.chance = v );
		}

		public LocationDef( ResourceLocation loot, List< ResourceLocation > chests, float chance ) {
			this.loot = loot;
			this.chests = chests;
			this.chance = chance;
		}

		public LocationDef() {
			this( null, List.of(), 0.0f );
		}
	}
}

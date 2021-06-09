package com.majruszs_difficulty;

import com.majruszs_difficulty.blocks.EndBlock;
import com.majruszs_difficulty.blocks.EndShardOre;
import com.majruszs_difficulty.blocks.InfestedEndStone;
import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.effects.BleedingImmunityEffect;
import com.majruszs_difficulty.entities.EntitiesConfig;
import com.majruszs_difficulty.events.ExperienceBonus;
import com.majruszs_difficulty.events.treasure_bag.FishingRewarder;
import com.majruszs_difficulty.events.IncreaseGameDifficulty;
import com.majruszs_difficulty.events.on_death.OnDeathEventHandler;
import com.majruszs_difficulty.events.on_death.SpawnPlayerZombieOnDeath;
import com.majruszs_difficulty.events.special.SplitCreeperToCreeperlings;
import com.majruszs_difficulty.events.special.StrongerExplosions;
import com.majruszs_difficulty.events.monster_spawn.*;
import com.majruszs_difficulty.events.undead_army.UndeadArmyConfig;
import com.majruszs_difficulty.events.when_damaged.*;
import com.majruszs_difficulty.generation.structure_pieces.FlyingEndShipPiece;
import com.majruszs_difficulty.generation.structures.FlyingEndShipStructure;
import com.majruszs_difficulty.items.*;
import com.majruszs_difficulty.generation.structure_pieces.FlyingEndIslandPiece;
import com.majruszs_difficulty.generation.structure_pieces.FlyingPhantomPiece;
import com.majruszs_difficulty.generation.structures.FlyingEndIslandStructure;
import com.majruszs_difficulty.generation.structures.FlyingPhantomStructure;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModLoadingContext;

public class Instances {
	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Items
	public static final UndeadBattleStandardItem BATTLE_STANDARD_ITEM;
	public static final BandageItem BANDAGE_ITEM;
	public static final FishermanEmblemItem FISHERMAN_EMBLEM_ITEM;
	public static final HermesBootsItem HERMES_BOOTS_ITEM;
	public static final EndShardItem END_SHARD_ITEM;
	public static final EndIngotItem END_INGOT_ITEM;
	public static final EndSwordItem END_SWORD_ITEM;
	public static final EndShovelItem END_SHOVEL_ITEM;
	public static final EndPickaxeItem END_PICKAXE_ITEM;
	public static final EndAxeItem END_AXE_ITEM;
	public static final EndHoeItem END_HOE_ITEM;
	public static final TatteredClothItem TATTERED_CLOTH_ITEM;
	public static final EndHelmetItem END_HELMET_ITEM;
	public static final EndChestplateItem END_CHESTPLATE_ITEM;
	public static final EndLeggingsItem END_LEGGINGS_ITEM;
	public static final EndBootsItem END_BOOTS_ITEM;
	public static final EndShardLocatorItem END_SHARD_LOCATOR_ITEM;
	public static final GiantSeedItem GIANT_SEED_ITEM;
	public static final LuckyRockItem LUCKY_ROCK_ITEM;
	public static final GoldenBandageItem GOLDEN_BANDAGE_ITEM;
	public static final IdolOfFertilityItem IDOL_OF_FERTILITY_ITEM;
	public static final TamingCertificateItem TAMING_CERTIFICATE_ITEM;

	// Treasure Bags
	public static final TreasureBagItem UNDEAD_ARMY_TREASURE_BAG;
	public static final TreasureBagItem ELDER_GUARDIAN_TREASURE_BAG;
	public static final TreasureBagItem WITHER_TREASURE_BAG;
	public static final TreasureBagItem ENDER_DRAGON_TREASURE_BAG;
	public static final TreasureBagItem FISHING_TREASURE_BAG;
	public static final TreasureBagItem PILLAGER_TREASURE_BAG;

	// Blocks
	public static final EndShardOre END_SHARD_ORE;
	public static final EndShardOre.EndShardOreItem END_SHARD_ORE_ITEM;
	public static final EndBlock END_BLOCK;
	public static final EndBlock.EndBlockItem END_BLOCK_ITEM;
	public static final InfestedEndStone INFESTED_END_STONE;
	public static final InfestedEndStone.InfestedEndStoneItem INFESTED_END_STONE_ITEM;

	// Entities
	public static final EntitiesConfig ENTITIES_CONFIG;

	// Effects
	public static final BleedingEffect BLEEDING;
	public static final BleedingImmunityEffect BLEEDING_IMMUNITY;

	// Damage sources
	public static final DamageSource BLEEDING_SOURCE;

	// Particles
	public static final BasicParticleType BLOOD_PARTICLE;

	// Structures
	public static final FlyingPhantomStructure FLYING_PHANTOM;
	public static final FlyingEndIslandStructure FLYING_END_ISLAND;
	public static final FlyingEndShipStructure FLYING_END_SHIP;
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FLYING_PHANTOM_FEATURE;
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FLYING_END_ISLAND_FEATURE;
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FLYING_END_SHIP_FEATURE;
	public static final IStructurePieceType FLYING_PHANTOM_PIECE;
	public static final IStructurePieceType FLYING_END_ISLAND_PIECE;
	public static final IStructurePieceType FLYING_END_SHIP_PIECE;

	// Events
	public static final StrongerExplosions STRONGER_EXPLOSIONS;
	public static final SplitCreeperToCreeperlings SPLIT_CREEPER_TO_CREEPERLINGS;

	// Misc
	public static final IncreaseGameDifficulty INCREASE_GAME_DIFFICULTY;
	public static final UndeadArmyConfig UNDEAD_ARMY_CONFIG;
	public static final ExperienceBonus EXPERIENCE_BONUS;
	public static final FishingRewarder FISHING_REWARDER;

	static {
		// Items
		BATTLE_STANDARD_ITEM = new UndeadBattleStandardItem();
		BANDAGE_ITEM = new BandageItem();
		FISHERMAN_EMBLEM_ITEM = new FishermanEmblemItem();
		HERMES_BOOTS_ITEM = new HermesBootsItem();
		END_SHARD_ITEM = new EndShardItem();
		END_INGOT_ITEM = new EndIngotItem();
		END_SWORD_ITEM = new EndSwordItem();
		END_SHOVEL_ITEM = new EndShovelItem();
		END_PICKAXE_ITEM = new EndPickaxeItem();
		END_AXE_ITEM = new EndAxeItem();
		END_HOE_ITEM = new EndHoeItem();
		TATTERED_CLOTH_ITEM = new TatteredClothItem();
		END_HELMET_ITEM = new EndHelmetItem();
		END_CHESTPLATE_ITEM = new EndChestplateItem();
		END_LEGGINGS_ITEM = new EndLeggingsItem();
		END_BOOTS_ITEM = new EndBootsItem();
		END_SHARD_LOCATOR_ITEM = new EndShardLocatorItem();
		GIANT_SEED_ITEM = new GiantSeedItem();
		LUCKY_ROCK_ITEM = new LuckyRockItem();
		GOLDEN_BANDAGE_ITEM = new GoldenBandageItem();
		IDOL_OF_FERTILITY_ITEM = new IdolOfFertilityItem();
		TAMING_CERTIFICATE_ITEM = new TamingCertificateItem();

		// Treasure Bags
		UNDEAD_ARMY_TREASURE_BAG = new TreasureBagItem( "undead_army", "Undead Army" );
		ELDER_GUARDIAN_TREASURE_BAG = new TreasureBagItem( "elder_guardian", "Elder Guardian" );
		WITHER_TREASURE_BAG = new TreasureBagItem( "wither", "Wither" );
		ENDER_DRAGON_TREASURE_BAG = new TreasureBagItem( "ender_dragon", "Ender Dragon" );
		FISHING_TREASURE_BAG = new TreasureBagItem( "fishing", "Fishing" );
		PILLAGER_TREASURE_BAG = new TreasureBagItem( "pillager", "Pillager Raid" );

		// Blocks
		END_SHARD_ORE = new EndShardOre();
		END_SHARD_ORE_ITEM = new EndShardOre.EndShardOreItem();
		END_BLOCK = new EndBlock();
		END_BLOCK_ITEM = new EndBlock.EndBlockItem();
		INFESTED_END_STONE = new InfestedEndStone();
		INFESTED_END_STONE_ITEM = new InfestedEndStone.InfestedEndStoneItem();

		// Entities
		ENTITIES_CONFIG = new EntitiesConfig();

		// Effects
		BLEEDING = new BleedingEffect();
		BLEEDING_IMMUNITY = new BleedingImmunityEffect();

		// Damage sources
		BLEEDING_SOURCE = new DamageSource( "bleeding" ).setDamageBypassesArmor();

		// Particles
		BLOOD_PARTICLE = new BasicParticleType( true );

		// Structures
		ResourceLocation flyingPhantomResource = MajruszsDifficulty.getLocation( "flying_phantom_structure" );
		FLYING_PHANTOM = new FlyingPhantomStructure();
		FLYING_PHANTOM_FEATURE = WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, flyingPhantomResource.toString(),
			FLYING_PHANTOM.withConfiguration( NoFeatureConfig.field_236559_b_ )
		);
		FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, flyingPhantomResource.toString() );

		ResourceLocation flyingEndIslandResource = MajruszsDifficulty.getLocation( "flying_end_island_structure" );
		FLYING_END_ISLAND = new FlyingEndIslandStructure();
		FLYING_END_ISLAND_FEATURE = WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, flyingEndIslandResource.toString(),
			FLYING_END_ISLAND.withConfiguration( NoFeatureConfig.field_236559_b_ )
		);
		FLYING_END_ISLAND_PIECE = IStructurePieceType.register( FlyingEndIslandPiece::new, flyingEndIslandResource.toString() );

		ResourceLocation flyingEndShipResource = MajruszsDifficulty.getLocation( "flying_end_ship_structure" );
		FLYING_END_SHIP = new FlyingEndShipStructure();
		FLYING_END_SHIP_FEATURE = WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, flyingEndShipResource.toString(),
			FLYING_END_SHIP.withConfiguration( NoFeatureConfig.field_236559_b_ )
		);
		FLYING_END_SHIP_PIECE = IStructurePieceType.register( FlyingEndShipPiece::new, flyingEndShipResource.toString() );

		// When damaged events
		WhenDamagedEvent.REGISTRY_LIST.add( new SpiderPoisonOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new SkyKeeperLevitationOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new DrownedLightningOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new NauseaAndWeaknessWhenDrowning() );
		WhenDamagedEvent.REGISTRY_LIST.add( new WitherSwordOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new CactusBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new SharpItemBleedingOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new ArrowBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new ThrownTridentBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new BiteBleedingOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new EndermanTeleportOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new EndSwordLevitationOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new TriggerAllEndermansOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new ShulkerBlindnessOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new NauseaAndSlownessWhenFalling() );
		WhenDamagedEvent.REGISTRY_LIST.add( new CreeperDamageReductionOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new IgniteCreeperOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new SlimeSlownessOnAttack() );

		// On enemy to be spawned
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new StrengthenedEntityAttributesOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new GiveWitherSkeletonSwordOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new GiveEvokerTotemOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new ChargeCreeperOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new ApplyingNegativeEffectOnCreeperOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnPiglinGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnPillagerGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnSkeletonGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnZombieGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new CreateJockeyOnSpiderSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnEliteSkeletonGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new AddAIToCreeperOnSpawn() );

		// On death events
		OnDeathEventHandler.REGISTRY_LIST.add( new SpawnPlayerZombieOnDeath() );

		// Events
		STRONGER_EXPLOSIONS = new StrongerExplosions();
		SPLIT_CREEPER_TO_CREEPERLINGS = new SplitCreeperToCreeperlings();

		// Misc
		INCREASE_GAME_DIFFICULTY = new IncreaseGameDifficulty();
		UNDEAD_ARMY_CONFIG = new UndeadArmyConfig();
		EXPERIENCE_BONUS = new ExperienceBonus();
		FISHING_REWARDER = new FishingRewarder();

		MajruszsDifficulty.CONFIG_HANDLER.register( ModLoadingContext.get() );
	}

	public static class Tools {
		public static final SwordItem WITHER_SWORD;

		static {
			WITHER_SWORD = new WitherSwordItem();
		}
	}

	public static class Sounds {
		public static final SoundEvent UNDEAD_ARMY_APPROACHING;
		public static final SoundEvent UNDEAD_ARMY_WAVE_STARTED;

		static {
			UNDEAD_ARMY_APPROACHING = new SoundEvent( MajruszsDifficulty.getLocation( "undead_army.approaching" ) );
			UNDEAD_ARMY_WAVE_STARTED = new SoundEvent( MajruszsDifficulty.getLocation( "undead_army.wave_started" ) );
		}
	}
}

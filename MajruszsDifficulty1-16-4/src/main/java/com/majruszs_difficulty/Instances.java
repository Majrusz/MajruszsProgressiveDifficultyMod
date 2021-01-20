package com.majruszs_difficulty;

import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.entities.EntitiesConfig;
import com.majruszs_difficulty.events.ExperienceBonus;
import com.majruszs_difficulty.events.FallDamageWithNegativeEffects;
import com.majruszs_difficulty.events.FishingRewarder;
import com.majruszs_difficulty.events.monster_spawn.*;
import com.majruszs_difficulty.events.undead_army.UndeadArmyConfig;
import com.majruszs_difficulty.events.when_damaged.*;
import com.majruszs_difficulty.items.*;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModLoadingContext;

public class Instances {
	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Entities
	public static final EntitiesConfig ENTITIES_CONFIG;

	// Effects
	public static final BleedingEffect BLEEDING;

	// Misc
	public static final UndeadArmyConfig UNDEAD_ARMY_CONFIG;
	public static final ExperienceBonus EXPERIENCE_BONUS;
	public static final FallDamageWithNegativeEffects FALL_DAMAGE_EFFECTS;
	public static final FishingRewarder FISHING_REWARDER;

	static {
		// Entities
		ENTITIES_CONFIG = new EntitiesConfig();

		// Effects
		BLEEDING = new BleedingEffect();

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

		// Misc
		UNDEAD_ARMY_CONFIG = new UndeadArmyConfig();
		EXPERIENCE_BONUS = new ExperienceBonus();
		FALL_DAMAGE_EFFECTS = new FallDamageWithNegativeEffects();
		FISHING_REWARDER = new FishingRewarder();

		MajruszsDifficulty.CONFIG_HANDLER.register( ModLoadingContext.get() );
	}

	public static class TreasureBags {
		public static final TreasureBagItem UNDEAD_ARMY;
		public static final TreasureBagItem ELDER_GUARDIAN;
		public static final TreasureBagItem WITHER;
		public static final TreasureBagItem ENDER_DRAGON;
		public static final TreasureBagItem FISHING;

		static {
			UNDEAD_ARMY = new TreasureBagItem( "undead_army" );
			ELDER_GUARDIAN = new TreasureBagItem( "elder_guardian" );
			WITHER = new TreasureBagItem( "wither" );
			ENDER_DRAGON = new TreasureBagItem( "ender_dragon" );
			FISHING = new TreasureBagItem( "fishing" );
		}
	}

	public static class Tools {
		public static final SwordItem WITHER_SWORD;

		static {
			WITHER_SWORD = new WitherSwordItem();
		}
	}

	public static class Miscellaneous {
		public static final UndeadBattleStandardItem BATTLE_STANDARD_ITEM;
		public static final BandageItem BANDAGE;

		static {
			BATTLE_STANDARD_ITEM = new UndeadBattleStandardItem();
			BANDAGE = new BandageItem();
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

	public static class Effects {
		public static final BleedingEffect BLEEDING;

		static {
			BLEEDING = new BleedingEffect();
		}
	}

	public static class DamageSources {
		public static final DamageSource BLEEDING;

		static {
			BLEEDING = new DamageSource( "bleeding" ).setDamageBypassesArmor();
		}
	}

	public static class Structures {
		public static final Structure< NoFeatureConfig > FLYING_PHANTOM;
		public static final IStructurePieceType FLYING_PHANTOM_PIECE;

		static {
			ResourceLocation flyingPhantomResource = MajruszsDifficulty.getLocation( "flying_phantom" );
			FLYING_PHANTOM = new FlyingPhantomStructure();
			FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, flyingPhantomResource.toString() );
		}
	}
}

package com.majruszs_difficulty;

import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.events.when_damaged.*;
import com.majruszs_difficulty.items.BandageItem;
import com.majruszs_difficulty.items.TreasureBagItem;
import com.majruszs_difficulty.items.UndeadBattleStandardItem;
import com.majruszs_difficulty.items.WitherSwordItem;
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

import static com.majruszs_difficulty.events.when_damaged.WhenDamagedEvent.REGISTRY_LIST;

public class Instances {
	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	static {
		// When damaged events
		REGISTRY_LIST.add( new SpiderPoisonOnAttack() );
		REGISTRY_LIST.add( new SkyKeeperLevitationOnAttack() );
		REGISTRY_LIST.add( new DrownedLightningOnAttack() );
		REGISTRY_LIST.add( new NauseaAndWeaknessWhenDrowning() );
		REGISTRY_LIST.add( new WitherSwordOnAttack() );
		REGISTRY_LIST.add( new CactusBleedingOnHurt() );
		REGISTRY_LIST.add( new SharpItemBleedingOnAttack() );
		REGISTRY_LIST.add( new ArrowBleedingOnHurt() );
		REGISTRY_LIST.add( new ThrownTridentBleedingOnHurt() );
		REGISTRY_LIST.add( new BiteBleedingOnAttack() );

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
			ResourceLocation flyingPhantomResource = MajruszsHelper.getResource( "flying_phantom" );
			FLYING_PHANTOM = new FlyingPhantomStructure();
			FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, flyingPhantomResource.toString() );
		}
	}
}

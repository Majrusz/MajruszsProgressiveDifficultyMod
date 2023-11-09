package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.FragileEndStone;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.FakeItem;
import com.mlib.modhelper.ModHelper;
import com.mlib.registry.RegistryGroup;
import com.mlib.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final ModHelper HELPER = ModHelper.create( MOD_ID );

	// Data
	public static final Config CONFIG = HELPER.config( Config::new ).autoSync().create();
	public static final WorldData WORLD_DATA = HELPER.saved( WorldData::new );

	// Registry Groups
	public static final RegistryGroup< Block > BLOCKS = HELPER.create( BuiltInRegistries.BLOCK );
	public static final RegistryGroup< Item > ITEMS = HELPER.create( BuiltInRegistries.ITEM );

	// Blocks
	public static final RegistryObject< FragileEndStone > FRAGILE_END_STONE = BLOCKS.create( "fragile_end_stone", FragileEndStone::new );
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );

	// Items
	public static final RegistryObject< FragileEndStone.Item > FRAGILE_END_STONE_ITEM = ITEMS.create( "fragile_end_stone", FragileEndStone.Item::new );
	public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE_ITEM = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );

	// Items (fake)
	static {
		ITEMS.create( "advancement_normal", FakeItem::new );
		ITEMS.create( "advancement_expert", FakeItem::new );
		ITEMS.create( "advancement_master", FakeItem::new );
	}

	// Placed Features
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone" ) );
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_LARGE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone_large" ) );
	public static final ResourceKey< PlacedFeature > INFESTED_END_STONE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "infested_end_stone" ) );

	// Advancements
	public static final GameStageAdvancement GAME_STAGE_ADVANCEMENT = new GameStageAdvancement();

	private MajruszsDifficulty() {}
}

package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.FakeItem;
import com.mlib.modhelper.ModHelper;
import com.mlib.registry.RegistryGroup;
import com.mlib.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );

	// Items
	public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE_ITEM = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );

	// Items (fake)
	static {
		ITEMS.create( "advancement_normal", FakeItem::new );
		ITEMS.create( "advancement_expert", FakeItem::new );
		ITEMS.create( "advancement_master", FakeItem::new );
	}

	// Advancements
	public static final GameStageAdvancement GAME_STAGE_ADVANCEMENT = new GameStageAdvancement();

	private MajruszsDifficulty() {}
}

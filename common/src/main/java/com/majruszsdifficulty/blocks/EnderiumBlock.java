package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class EnderiumBlock extends Block {
	public EnderiumBlock() {
		super( Properties.of().mapColor( MapColor.COLOR_PURPLE ).requiresCorrectToolForDrops().strength( 5.0f, 6.0f ).sound( SoundType.METAL ) );
	}

	public static class Item extends BlockItem {
		public Item() {
			super( MajruszsDifficulty.ENDERIUM_BLOCK.get(), new Properties().stacksTo( 64 ).rarity( Rarity.UNCOMMON ) );
		}
	}
}

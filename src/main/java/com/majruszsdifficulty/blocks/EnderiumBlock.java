package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class EnderiumBlock extends Block {
	public EnderiumBlock() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_PURPLE ).requiresCorrectToolForDrops().strength( 5.0f, 6.0f ).sound( SoundType.METAL ) );
	}

	public static class EndBlockItem extends BlockItem {
		public EndBlockItem() {
			super( Registries.ENDERIUM_BLOCK.get(), new Properties().stacksTo( 64 ).rarity( Rarity.UNCOMMON ) );
		}
	}
}

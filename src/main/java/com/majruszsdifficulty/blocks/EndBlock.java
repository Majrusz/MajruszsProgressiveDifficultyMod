package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/** New late game end block made from End Ingots. */
public class EndBlock extends Block {
	public EndBlock() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_PURPLE ).requiresCorrectToolForDrops().strength( 5.0f, 6.0f ).sound( SoundType.METAL ) );
	}

	public static class EndBlockItem extends BlockItem {
		public EndBlockItem() {
			super( Registries.END_BLOCK.get(), ( new Properties() ).stacksTo( 64 ).rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ) );
		}
	}
}

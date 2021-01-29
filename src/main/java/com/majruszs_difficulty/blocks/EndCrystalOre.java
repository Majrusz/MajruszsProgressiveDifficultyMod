package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;

/** New late game crystal ore located in The End. */
public class EndCrystalOre extends Block {
	public EndCrystalOre() {
		super( AbstractBlock.Properties.create( Material.IRON, MaterialColor.YELLOW )
			.harvestLevel( 3 )
			.setRequiresTool()
			.hardnessAndResistance( 30.0F, 1200.0F )
			.sound( SoundType.ANCIENT_DEBRIS )
		);
	}

	public static class EndCrystalOreItem extends BlockItem {
		public EndCrystalOreItem() {
			super( Instances.END_CRYSTAL_ORE, ( new Properties() ).maxStackSize( 64 )
				.group( Instances.ITEM_GROUP ) );
		}
	}
}

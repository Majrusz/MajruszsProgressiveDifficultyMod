package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game end block. */
public class EndBlock extends Block {
	public EndBlock() {
		super( Properties.create( Material.IRON, MaterialColor.PURPLE )
			.harvestLevel( 1 )
			.setRequiresTool()
			.hardnessAndResistance( 5.0f, 6.0f )
			.sound( SoundType.METAL ) );
	}

	public static class EndBlockItem extends BlockItem {
		public EndBlockItem() {
			super( Instances.END_BLOCK, ( new Properties() ).maxStackSize( 64 )
				.rarity( Rarity.UNCOMMON )
				.group( Instances.ITEM_GROUP )
			);
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
			MajruszsDifficulty.addExtraTooltipIfDisabled( toolTip, Instances.END_SHARD_ORE.isEnabled() );
		}
	}
}

package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game end block made from End Ingots. */
public class EndBlock extends Block {
	public EndBlock() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_PURPLE )
			.harvestLevel( 4 )
			.requiresCorrectToolForDrops()
			.strength( 5.0f, 6.0f )
			.sound( SoundType.METAL ) );
	}

	public static class EndBlockItem extends BlockItem {
		public EndBlockItem() {
			super( Instances.END_BLOCK, ( new Properties() ).stacksTo( 64 )
				.rarity( Rarity.UNCOMMON )
				.tab( Instances.ITEM_GROUP ) );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
			MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		}
	}
}

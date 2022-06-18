package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
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
		super( Properties.of( Material.METAL, MaterialColor.COLOR_PURPLE ).requiresCorrectToolForDrops().strength( 5.0f, 6.0f ).sound( SoundType.METAL ) );
	}

	public static class EndBlockItem extends BlockItem {
		public EndBlockItem() {
			super( Registries.END_BLOCK.get(), ( new Properties() ).stacksTo( 64 ).rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ) );
		}
	}
}

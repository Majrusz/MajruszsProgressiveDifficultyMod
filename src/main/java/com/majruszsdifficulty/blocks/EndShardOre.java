package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EndShardOre extends Block {
	public EndShardOre() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_YELLOW )
			.requiresCorrectToolForDrops()
			.strength( 30.0f, 1200.0f )
			.sound( SoundType.ANCIENT_DEBRIS ) );
	}

	@Override
	protected void tryDropExperience( ServerLevel level, BlockPos position, ItemStack itemStack, IntProvider intProvider ) {
		if( itemStack.getEnchantmentLevel( Enchantments.SILK_TOUCH ) == 0 ) {
			this.popExperience( level, position, Random.nextInt( 6, 11 ) );
		}
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Registries.ENDERIUM_SHARD_ORE.get(), new Properties().stacksTo( 64 ).tab( Registries.ITEM_GROUP ) );
		}
	}
}

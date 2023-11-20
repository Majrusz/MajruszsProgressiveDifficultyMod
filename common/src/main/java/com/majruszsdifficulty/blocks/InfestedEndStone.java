package com.majruszsdifficulty.blocks;

import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class InfestedEndStone extends Block {
	public InfestedEndStone() {
		super( Properties.of().mapColor( MapColor.SAND ).strength( 0.0f, 0.75f ).sound( SoundType.STONE ) );
	}

	@Override
	public void spawnAfterBreak( BlockState blockState, ServerLevel level, BlockPos blockPos, ItemStack itemStack, boolean p_221364_ ) {
		super.spawnAfterBreak( blockState, level, blockPos, itemStack, p_221364_ );

		this.tryToSpawnEndermite( level, itemStack, blockPos );
	}

	@Override
	public void wasExploded( Level level, BlockPos blockPos, Explosion explosion ) {
		super.wasExploded( level, blockPos, explosion );

		this.tryToSpawnEndermite( level, ItemStack.EMPTY, blockPos );
	}

	private void tryToSpawnEndermite( Level level, ItemStack itemStack, BlockPos blockPos ) {
		if( !( level instanceof ServerLevel ) ) {
			return;
		}

		if( !level.getGameRules().getBoolean( GameRules.RULE_DOBLOCKDROPS ) ) {
			return;
		}

		if( EnchantmentHelper.getItemEnchantmentLevel( Enchantments.SILK_TOUCH, itemStack ) != 0 ) {
			return;
		}

		EntityHelper.createSpawner( ()->EntityType.ENDERMITE, level )
			.position( AnyPos.from( blockPos ).center().vec3() )
			.beforeEvent( Mob::spawnAnim )
			.spawn();
	}

	public static class Item extends BlockItem {
		public Item() {
			super( MajruszsDifficulty.INFESTED_END_STONE.get(), new Properties().stacksTo( 64 ) );
		}
	}
}

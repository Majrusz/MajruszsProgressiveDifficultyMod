package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
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

/** New end block which works and looks like End Stone but spawns the Endermite when destroyed. */
public class InfestedEndStone extends Block {
	public InfestedEndStone() {
		super( Properties.of().mapColor( MapColor.SAND ).strength( 0.0f, 0.75f ).sound( SoundType.STONE ) );
	}

	@Override
	public void spawnAfterBreak( BlockState state, ServerLevel worldIn, BlockPos pos, ItemStack stack, boolean p_221364_ ) {
		super.spawnAfterBreak( state, worldIn, pos, stack, p_221364_ );

		GameRules gameRules = worldIn.getGameRules();
		if( gameRules.getBoolean( GameRules.RULE_DOBLOCKDROPS ) && EnchantmentHelper.getItemEnchantmentLevel( Enchantments.SILK_TOUCH, stack ) == 0 )
			this.spawnEndermite( worldIn, pos );
	}

	@Override
	public void wasExploded( Level world, BlockPos position, Explosion explosion ) {
		if( world instanceof ServerLevel )
			this.spawnEndermite( ( ServerLevel )world, position );
	}

	private void spawnEndermite( ServerLevel world, BlockPos position ) {
		Endermite endermite = EntityType.ENDERMITE.create( world );
		if( endermite == null )
			return;

		endermite.moveTo( position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0.0f, 0.0f );
		world.addFreshEntity( endermite );
		endermite.spawnAnim();
	}

	public static class InfestedEndStoneItem extends BlockItem {
		public InfestedEndStoneItem() {
			super( Registries.INFESTED_END_STONE.get(), ( new Properties() ).stacksTo( 64 ) );
		}
	}
}

package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/** New end block which works like End Stone block but spawns Endermite when destroyed. */
public class InfestedEndStone extends Block {
	public InfestedEndStone() {
		super( Properties.create( Material.CLAY, MaterialColor.YELLOW )
			.hardnessAndResistance( 0.0f, 0.75f )
			.sound( SoundType.STONE ) );
	}

	@Override
	public void spawnAdditionalDrops( BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack ) {
		super.spawnAdditionalDrops( state, worldIn, pos, stack );

		GameRules gameRules = worldIn.getGameRules();
		if( gameRules.getBoolean( GameRules.DO_TILE_DROPS ) && EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH, stack ) == 0 )
			this.spawnEndermite( worldIn, pos );
	}

	@Override
	public void onExplosionDestroy( World world, BlockPos position, Explosion explosion ) {
		if( world instanceof ServerWorld )
			this.spawnEndermite( ( ServerWorld )world, position );
	}

	/** Spawns a Endermite at given position. */
	private void spawnEndermite( ServerWorld world, BlockPos position ) {
		EndermiteEntity endermite = EntityType.ENDERMITE.create( world );
		if( endermite == null )
			return;

		endermite.setLocationAndAngles( position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0.0f, 0.0f );
		world.addEntity( endermite );
		endermite.spawnExplosionParticle();
	}

	public static class InfestedEndStoneItem extends BlockItem {
		public InfestedEndStoneItem() {
			super( Instances.INFESTED_END_STONE, ( new Properties() ).maxStackSize( 64 )
				.group( Instances.ITEM_GROUP ) );
		}
	}
}

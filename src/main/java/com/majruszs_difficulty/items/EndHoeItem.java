package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.features.end_items.EndItems;
import com.mlib.MajruszLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/** New late game hoe. */
public class EndHoeItem extends HoeItem {
	public EndHoeItem() {
		super( CustomItemTier.END, -5, 0.0f, ( new Properties() ).tab( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.fireResistant() );
	}

	@Override
	public InteractionResult useOn( UseOnContext context ) {
		Player player = context.getPlayer();
		InteractionResult resultType = super.useOn( context );

		// if( player != null && !player.isCrouching() && resultType == InteractionResult.CONSUME )
		// tillNearbyBlocks( context );

		return resultType;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltips( tooltip, flag, EndItems.Keys.BLEED_TOOLTIP, EndItems.Keys.HASTE_TOOLTIP, EndItems.Keys.LEVITATION_TOOLTIP/*,
			EndItems.Keys.TILL_TOOLTIP*/ );
	}

	/** Tills nearby blocks in 3x1x3 grid around the context. */
	protected void tillNearbyBlocks( UseOnContext context ) {
		Level world = context.getLevel();
		BlockPos blockPosition = context.getClickedPos();
		Player player = context.getPlayer();
		ItemStack itemStack = context.getItemInHand();
		// Pair< Predicate< UseOnContext >, Consumer< UseOnContext > > pair = TILLABLES.get( world.getBlockState( blockPosition ).getBlock() );
		// Predicate< UseOnContext > predicate = pair.getFirst();
		// Consumer< UseOnContext > consumer = pair.getSecond();

		for( int x = -1; x <= 1; ++x )
			for( int z = -1; z <= 1; ++z ) {
				if( x == 0 && z == 0 )
					continue;
				MajruszLibrary.LOGGER.info( x + "," + z );
				BlockPos newPosition = blockPosition.offset( x, 0, z );
				if( !world.isEmptyBlock( newPosition.above() ) )
					continue;

				MajruszLibrary.LOGGER.info( newPosition );
				MajruszLibrary.LOGGER.info( "isEmpty " + world.getBlockState( newPosition ) + " " + world.getBlockState( newPosition )
					.getToolModifiedState( world, newPosition, player, itemStack, ToolType.HOE ) );
				BlockState blockState = world.getBlockState( newPosition )
					.getToolModifiedState( world, newPosition, player, itemStack, ToolType.HOE );
				MajruszLibrary.LOGGER.info( ( blockState == null ) + " " + ( world.isClientSide ) );
				if( blockState == null || world.isClientSide )
					continue;

				MajruszLibrary.LOGGER.info( "set" );
				world.setBlock( newPosition, blockState, 11 );
				if( player != null )
					itemStack.hurtAndBreak( 1, player, user->user.broadcastBreakEvent( context.getHand() ) );
			}
	}
}

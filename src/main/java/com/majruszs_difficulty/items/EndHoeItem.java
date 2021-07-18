package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.features.HasteOnDestroyingBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/** New late game hoe. */
public class EndHoeItem extends HoeItem {
	public EndHoeItem() {
		super( CustomItemTier.END, -5, 0.0f, ( new Properties() ).group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.isImmuneToFire() );
	}

	@Override
	public ActionResultType onItemUse( ItemUseContext context ) {
		PlayerEntity player = context.getPlayer();
		ActionResultType resultType = super.onItemUse( context );

		if( player != null && !player.isCrouching() && resultType == ActionResultType.CONSUME )
			tillNearbyBlocks( context );

		return resultType;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, HasteOnDestroyingBlocks.getTooltipTranslationKey() );
	}

	/** Tills nearby blocks in 3x1x3 grid around the context. */
	protected void tillNearbyBlocks( ItemUseContext context ) {
		World world = context.getWorld();
		BlockPos blockPosition = context.getPos();
		PlayerEntity player = context.getPlayer();
		ItemStack itemStack = context.getItem();

		for( int x = -1; x <= 1; ++x )
			for( int z = -1; z <= 1; ++z ) {
				if( x == 0 && z == 0 )
					continue;

				BlockPos newPosition = blockPosition.add( x, 0, z );
				if( !world.isAirBlock( newPosition.up() ) )
					continue;

				BlockState blockState = world.getBlockState( newPosition )
					.getToolModifiedState( world, newPosition, player, itemStack, ToolType.HOE );
				if( blockState == null || world.isRemote )
					continue;

				world.setBlockState( newPosition, blockState, 11 );
				if( player != null )
					itemStack.damageItem( 1, player, user->user.sendBreakAnimation( context.getHand() ) );
			}
	}
}

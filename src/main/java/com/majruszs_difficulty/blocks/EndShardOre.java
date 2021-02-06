package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.mlib.MajruszLibrary;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** New late game crystal ore located in The End. */
@Mod.EventBusSubscriber
public class EndShardOre extends Block {
	public EndShardOre() {
		super( AbstractBlock.Properties.create( Material.IRON, MaterialColor.YELLOW )
			.harvestLevel( 4 )
			.setRequiresTool()
			.hardnessAndResistance( 30.0f, 1200.0f )
			.sound( SoundType.ANCIENT_DEBRIS ) );
	}

	@Override
	public int getExpDrop( BlockState state, net.minecraft.world.IWorldReader world, BlockPos position, int fortuneLevel, int silkTouchLevel ) {
		return silkTouchLevel == 0 ? MathHelper.nextInt( MajruszLibrary.RANDOM, 6, 11 ) : 0;
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		BlockState blockState = event.getState();

		if( blockState.getBlock() instanceof EndShardOre )
			targetEndermansOnEntity( event.getPlayer(), 2000.0 );
	}

	/**
	 Makes all endermans in the given distance target the player.

	 @param player          Player to target.
	 @param maximumDistance Maximum distance from enderman to player.
	 */
	private static void targetEndermansOnEntity( PlayerEntity player, double maximumDistance ) {
		if( !( player.world instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )player.world;
		for( Entity entity : world.getEntities( null, enderman->enderman.getDistanceSq( player ) < maximumDistance ) )
			if( entity instanceof EndermanEntity ) {
				EndermanEntity enderman = ( EndermanEntity )entity;
				enderman.setRevengeTarget( player );
			}
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Instances.END_SHARD_ORE, ( new Properties() ).maxStackSize( 64 )
				.group( Instances.ITEM_GROUP ) );
		}
	}
}

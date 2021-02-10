package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.mlib.MajruszLibrary;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
	public static void onBlockDestroying( PlayerEvent.BreakSpeed event ) {
		BlockState blockState = event.getState();
		Block block = blockState.getBlock();
		if( block.equals( Instances.END_SHARD_ORE ) ) {
			PlayerEntity player = event.getPlayer();
			player.sendStatusMessage( new TranslationTextComponent( "block.majruszs_difficulty.end_shard_ore.warning" ).mergeStyle( TextFormatting.BOLD ), true );
		}
	}
	
	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		BlockState blockState = event.getState();

		if( blockState.getBlock() instanceof EndShardOre )
			targetEndermansOnEntity( event.getPlayer(), 1000.0 );
	}

	/**
	 Makes all endermans in the given distance target the entity.

	 @param target          Entity to target.
	 @param maximumDistance Maximum distance from enderman to entity.
	 */
	public static void targetEndermansOnEntity( LivingEntity target, double maximumDistance ) {
		if( !( target.world instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )target.world;
		for( Entity entity : world.getEntities( null, enderman->enderman.getDistanceSq( target ) < maximumDistance ) )
			if( entity instanceof EndermanEntity ) {
				EndermanEntity enderman = ( EndermanEntity )entity;
				LivingEntity currentEndermanTarget = enderman.getRevengeTarget();
				if( currentEndermanTarget == null || !currentEndermanTarget.isAlive() )
					enderman.setRevengeTarget( target );
			}
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Instances.END_SHARD_ORE, ( new Properties() ).maxStackSize( 64 )
				.group( Instances.ITEM_GROUP ) );
		}
	}
}

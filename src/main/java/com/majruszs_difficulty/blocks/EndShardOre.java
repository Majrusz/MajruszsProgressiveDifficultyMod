package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.mlib.MajruszLibrary;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** New late game crystal ore located in The End. */
@Mod.EventBusSubscriber
public class EndShardOre extends Block {
	private static final String WARNING_TRANSLATION_KEY = "block.majruszs_difficulty.end_shard_ore.warning";
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig availability;
	protected final DoubleConfig triggerDistance;

	public EndShardOre() {
		super( AbstractBlock.Properties.create( Material.IRON, MaterialColor.YELLOW )
			.harvestLevel( 4 )
			.setRequiresTool()
			.hardnessAndResistance( 30.0f, 1200.0f )
			.sound( SoundType.ANCIENT_DEBRIS ) );

		String availabilityComment = "Should this ore be available in survival mode? (ore generation, loot tables etc.) (requires game restart!)";
		String triggerComment = "Maximum distance within which all nearby Endermans will be triggered. (when block was mined)";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, true, true );
		this.triggerDistance = new DoubleConfig( "trigger_distance", triggerComment, false, 700.0, 0.0, 1e+6 );

		this.configGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "EndShardOre", "Configuration for new late game ore." ) );
		this.configGroup.addConfigs( this.availability, this.triggerDistance );
	}

	@Override
	public int getExpDrop( BlockState state, net.minecraft.world.IWorldReader world, BlockPos position, int fortuneLevel, int silkTouchLevel ) {
		return silkTouchLevel == 0 ? MathHelper.nextInt( MajruszLibrary.RANDOM, 6, 11 ) : 0;
	}

	@SubscribeEvent
	public static void onBlockDestroying( PlayerEvent.BreakSpeed event ) {
		BlockState blockState = event.getState();
		if( !( blockState.getBlock() instanceof EndShardOre ) )
			return;

		PlayerEntity player = event.getPlayer();
		player.sendStatusMessage( new TranslationTextComponent( WARNING_TRANSLATION_KEY ).mergeStyle( TextFormatting.BOLD ), true );
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		BlockState blockState = event.getState();

		if( blockState.getBlock() instanceof EndShardOre )
			targetEndermansOnEntity( event.getPlayer(), Instances.END_SHARD_ORE.triggerDistance.get() );
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

	/** Returns whether End Shard Ore is enabled in config file. */
	public boolean isEnabled() {
		return this.availability.isEnabled();
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Instances.END_SHARD_ORE, ( new Properties() ).maxStackSize( 64 )
				.group( Instances.ITEM_GROUP ) );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
			MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		}
	}
}

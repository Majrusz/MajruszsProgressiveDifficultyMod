package com.majruszs_difficulty.blocks;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.mlib.MajruszLibrary;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
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
		super( Properties.of( Material.METAL, MaterialColor.COLOR_YELLOW )
			.requiresCorrectToolForDrops()
			.strength( 30.0f, 1200.0f )
			.sound( SoundType.ANCIENT_DEBRIS ) );

		String availabilityComment = "Should this ore be available in survival mode? (ore generation, loot tables etc.) (requires game restart!)";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, true, true );

		String triggerComment = "Maximum distance within which all nearby Endermans are being triggered. (when block was mined)";
		this.triggerDistance = new DoubleConfig( "trigger_distance", triggerComment, false, 450.0, 0.0, 1e+6 );

		this.configGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "EndShardOre", "Configuration for new late game ore." ) );
		this.configGroup.addConfigs( this.availability, this.triggerDistance );
	}

	@Override
	public int getExpDrop( BlockState state, LevelReader world, BlockPos position, int fortuneLevel, int silkTouchLevel ) {
		return silkTouchLevel == 0 ? Mth.nextInt( MajruszLibrary.RANDOM, 6, 11 ) : 0;
	}

	@SubscribeEvent
	public static void onBlockDestroying( PlayerEvent.BreakSpeed event ) {
		BlockState blockState = event.getState();
		if( !( blockState.getBlock() instanceof EndShardOre ) )
			return;

		Player player = event.getPlayer();
		player.displayClientMessage( new TranslatableComponent( WARNING_TRANSLATION_KEY ).withStyle( ChatFormatting.BOLD ), true );
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
		if( !( target.level instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )target.level;
		AABB aabb = target.getBoundingBox().inflate( 100.0, 16.0, 100.0 );
		for( EnderMan enderman : world.getEntitiesOfClass( EnderMan.class, aabb, enderman->enderman.distanceToSqr( target ) < maximumDistance ) ) {
			LivingEntity currentEndermanTarget = enderman.getLastHurtByMob();
			if( currentEndermanTarget == null || !currentEndermanTarget.isAlive() )
				enderman.setLastHurtByMob( target );
		}
	}

	/** Returns whether End Shard Ore is enabled in config file. */
	public boolean isEnabled() {
		return this.availability.isEnabled();
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Instances.END_SHARD_ORE, ( new Properties() ).stacksTo( 64 ).tab( Instances.ITEM_GROUP ) );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
			MajruszsHelper.addExtraTextIfItemIsDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		}
	}
}

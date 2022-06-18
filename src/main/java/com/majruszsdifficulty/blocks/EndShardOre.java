package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

/** New late game crystal ore located in The End. */
@Mod.EventBusSubscriber
public class EndShardOre extends Block {
	private static final String WARNING_TRANSLATION_KEY = "block.majruszsdifficulty.end_shard_ore.warning";
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig availability;
	protected final DoubleConfig triggerDistance;

	public EndShardOre() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_YELLOW ).requiresCorrectToolForDrops().strength( 30.0f, 1200.0f ).sound( SoundType.ANCIENT_DEBRIS ) );

		String availabilityComment = "Should this ore be available in survival mode? (ore generation, loot tables etc.) (requires game restart!)";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, true, true );

		String triggerComment = "Maximum distance within which all nearby Endermans are being triggered. (when block was mined)";
		this.triggerDistance = new DoubleConfig( "trigger_distance", triggerComment, false, 450.0, 0.0, 1e+6 );

		this.configGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "EndShardOre", "Configuration for new late game ore." ) );
		this.configGroup.addConfigs( this.availability, this.triggerDistance );
	}

	@Override
	protected void tryDropExperience( ServerLevel level, BlockPos position, ItemStack itemStack, IntProvider intProvider ) {
		if( EnchantmentHelper.getItemEnchantmentLevel( Enchantments.SILK_TOUCH, itemStack ) == 0 ) {
			this.popExperience( level, position, Random.nextInt( 6, 11 ) );
		}
	}

	@SubscribeEvent
	public static void onBlockDestroying( PlayerEvent.BreakSpeed event ) {
		BlockState blockState = event.getState();
		if( !( blockState.getBlock() instanceof EndShardOre ) )
			return;

		Player player = event.getPlayer();
		player.displayClientMessage( Component.translatable( WARNING_TRANSLATION_KEY ).withStyle( ChatFormatting.BOLD ), true );
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		BlockState blockState = event.getState();

		if( blockState.getBlock() instanceof EndShardOre )
			targetEndermansOnEntity( event.getPlayer(), Registries.END_SHARD_ORE.get().triggerDistance.get() );
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
			super( Registries.END_SHARD_ORE.get(), ( new Properties() ).stacksTo( 64 ).tab( Registries.ITEM_GROUP ) );
		}
	}
}

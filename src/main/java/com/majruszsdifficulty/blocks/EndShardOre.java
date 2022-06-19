package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

@Mod.EventBusSubscriber
public class EndShardOre extends Block {
	private static final String WARNING_TRANSLATION_KEY = "block.majruszsdifficulty.enderium_shard_ore.warning";
	protected final ConfigGroup configGroup;
	protected final DoubleConfig triggerDistance;

	public EndShardOre() {
		super( Properties.of( Material.METAL, MaterialColor.COLOR_YELLOW )
			.requiresCorrectToolForDrops()
			.strength( 30.0f, 1200.0f )
			.sound( SoundType.ANCIENT_DEBRIS ) );

		this.triggerDistance = new DoubleConfig( "trigger_distance", "Maximum distance within which all nearby Endermans will start to attack the player once the block is mined.", false, 450.0, 0.0, 1e+6 );
		this.configGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "EndShardOre", "Configuration for new late game ore.", this.triggerDistance ) );
	}

	@Override
	protected void tryDropExperience( ServerLevel level, BlockPos position, ItemStack itemStack, IntProvider intProvider ) {
		if( EnchantmentHelper.getItemEnchantmentLevel( Enchantments.SILK_TOUCH, itemStack ) == 0 ) {
			this.popExperience( level, position, Random.nextInt( 6, 11 ) );
		}
	}

	@SubscribeEvent
	public static void onBlockDestroying( PlayerEvent.BreakSpeed event ) {
		if( !( event.getState().getBlock() instanceof EndShardOre ) )
			return;

		event.getPlayer().displayClientMessage( Component.translatable( WARNING_TRANSLATION_KEY ).withStyle( ChatFormatting.BOLD ), true );
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.BreakEvent event ) {
		if( !( event.getState().getBlock() instanceof EndShardOre ) )
			return;

		focusNearbyEndermansOnEntity( event.getPlayer(), Registries.ENDERIUM_SHARD_ORE.get().triggerDistance.get() );
	}

	/**
	 Makes all endermans in the given distance target the entity.

	 @param target          Entity to target.
	 @param maximumDistance Maximum distance from enderman to entity.
	 */
	public static void focusNearbyEndermansOnEntity( LivingEntity target, double maximumDistance ) {
		if( !( target.level instanceof ServerLevel world ) )
			return;

		AABB aabb = target.getBoundingBox().inflate( 100.0, 16.0, 100.0 );
		for( EnderMan enderman : world.getEntitiesOfClass( EnderMan.class, aabb, enderman->enderman.distanceToSqr( target ) < maximumDistance ) ) {
			LivingEntity currentEndermanTarget = enderman.getLastHurtByMob();
			if( currentEndermanTarget == null || !currentEndermanTarget.isAlive() )
				enderman.setLastHurtByMob( target );
		}
	}

	public static class EndShardOreItem extends BlockItem {
		public EndShardOreItem() {
			super( Registries.ENDERIUM_SHARD_ORE.get(), new Properties().stacksTo( 64 ).tab( Registries.ITEM_GROUP ) );
		}
	}
}

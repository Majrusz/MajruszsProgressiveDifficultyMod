package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.mlib.Random;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.events.HarvestCropEvent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Seed that gives a chance for double loot from crops. */
@Mod.EventBusSubscriber
public class GiantSeedItem extends Item {
	protected final ConfigGroup group;
	protected final DoubleConfig dropChance;
	protected final GameStateDoubleConfig chance;
	protected final AvailabilityConfig alwaysDrops;

	public GiantSeedItem() {
		super( ( new Properties() ).maxStackSize( 1 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP ) );

		String dropComment = "Chance for Giant Seed to drop from harvesting.";
		String chanceComment = "Chance for double loot when harvesting crops.";
		String alwaysComment = "Should Giant Seed drop even though player already has one in inventory?";
		String groupComment = "Functionality of Giant Seed.";
		this.dropChance = new DoubleConfig( "drop_chance", dropComment, false, 0.005, 0.0, 1.0 );
		this.chance = new GameStateDoubleConfig( "Chance", chanceComment, 0.15, 0.3, 0.45, 0.0, 1.0 );
		this.alwaysDrops = new AvailabilityConfig( "always_drops", alwaysComment, false, true );

		this.group = FEATURES_GROUP.addGroup( new ConfigGroup( "GiantSeed", groupComment ) );
		this.group.addConfigs( this.dropChance, this.chance, this.alwaysDrops );
	}

	/** Adding tooltip for what this seed does. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.giant_seed.item_tooltip" ).mergeStyle( TextFormatting.GOLD ) );

		if( !flag.isAdvanced() )
			return;

		toolTip.add( new StringTextComponent( " " ) );
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.items.inventory_item" ).mergeStyle( TextFormatting.GRAY ) );
	}

	@SubscribeEvent
	public static void handleHarvesting( HarvestCropEvent event ) {
		GiantSeedItem giantSeed = Instances.GIANT_SEED_ITEM;
		PlayerEntity player = event.getPlayer();

		if( !( player.world instanceof ServerWorld && event.crops.isMaxAge( event.blockState ) ) )
			return;

		if( giantSeed.shouldDrop( player ) )
			if( Random.tryChance( giantSeed.getDropChance() ) )
				event.generatedLoot.add( new ItemStack( giantSeed, 1 ) );

		if( giantSeed.hasAny( player ) )
			if( Random.tryChance( giantSeed.getDoubleLootChance() ) ) {
				event.generatedLoot.addAll( new ArrayList<>( event.generatedLoot ) );

				ServerWorld world = ( ServerWorld )player.world;
				Vector3d position = event.origin;
				world.spawnParticle( ParticleTypes.HAPPY_VILLAGER, position.getX(), position.getY(), position.getZ(), 5, 0.25, 0.25, 0.25, 0.1 );
			}
	}

	/** Returns current chance for double crops. */
	public double getDoubleLootChance() {
		return this.chance.getCurrentGameStateValue();
	}

	/** Returns a chance for Giant Seed to drop. */
	public double getDropChance() {
		return this.dropChance.get();
	}

	/** Checks whether player does not have Giant Seed in inventory or it should always drop. */
	public boolean shouldDrop( PlayerEntity player ) {
		return !hasAny( player ) || this.alwaysDrops.isEnabled();
	}

	/** Checks whether player have any Giant Seed in inventory. */
	public boolean hasAny( PlayerEntity player ) {
		Set< Item > items = new HashSet<>();
		items.add( this );

		return player.inventory.hasAny( items );
	}
}

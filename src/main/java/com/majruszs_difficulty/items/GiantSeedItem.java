package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.mlib.Random;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.events.HarvestCropEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

/** Seed that gives a chance for double loot from crops. */
@Mod.EventBusSubscriber
public class GiantSeedItem extends InventoryItem {
	protected final DoubleConfig dropChance;
	protected final GameStateDoubleConfig chance;
	protected final AvailabilityConfig alwaysDrops;

	public GiantSeedItem() {
		super( "Giant Seed", "giant_seed" );

		String dropComment = "Chance for Giant Seed to drop from harvesting.";
		String chanceComment = "Chance for double loot when harvesting crops.";
		String alwaysComment = "Should Giant Seed drop even though player already has one in inventory?";
		this.dropChance = new DoubleConfig( "drop_chance", dropComment, false, 0.005, 0.0, 1.0 );
		this.chance = new GameStateDoubleConfig( "Chance", chanceComment, 0.25, 0.4, 0.55, 0.0, 1.0 );
		this.alwaysDrops = new AvailabilityConfig( "always_drops", alwaysComment, false, true );

		this.group.addConfigs( this.dropChance, this.chance, this.alwaysDrops );
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
		return hasAny( player, this );
	}
}

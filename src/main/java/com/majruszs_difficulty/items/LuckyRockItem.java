package com.majruszs_difficulty.items;

import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;

/** Rock that gives a chance for extra drops from stone. */
@Mod.EventBusSubscriber
public class LuckyRockItem extends InventoryItem {
	protected final DoubleConfig dropChance;
	protected final GameStateDoubleConfig chance;

	public LuckyRockItem() {
		super( "Lucky Rock", "lucky_rock" );

		String dropComment = "Chance for Lucky Rock to drop from mining.";
		String chanceComment = "Chance for extra loot when mining.";
		this.dropChance = new DoubleConfig( "drop_chance", dropComment, false, 0.005, 0.0, 1.0 );
		this.chance = new GameStateDoubleConfig( "Chance", chanceComment, 0.005, 0.01, 0.015, 0.0, 1.0 );

		this.group.addConfigs( this.dropChance, this.chance );
	}

	/** Returns current chance for extra loot from mining. */
	public double getExtraLootChance() {
		return this.chance.getCurrentGameStateValue();
	}

	/** Returns a chance for Lucky Rock to drop. */
	public double getDropChance() {
		return this.dropChance.get();
	}

	/** Checks whether player has any Lucky Rock in inventory. */
	public boolean hasAny( PlayerEntity player ) {
		return hasAny( player, this );
	}
}

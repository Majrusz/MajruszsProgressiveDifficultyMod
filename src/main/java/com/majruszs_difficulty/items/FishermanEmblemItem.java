package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.attributes.AttributeHandler;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Emblem that increases luck for player that has it anywhere in the inventory. */
@Mod.EventBusSubscriber
public class FishermanEmblemItem extends InventoryItem {
	protected static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "4010270c-9d57-4273-8a41-00985f1e4781", "FishermanEmblemLuckBonus",
		Attributes.LUCK, AttributeModifier.Operation.ADDITION
	);
	protected final GameStateIntegerConfig luck;

	public FishermanEmblemItem() {
		super( "Fisherman Emblem", "fisherman_emblem" );

		String luckComment = "Luck bonus when fishing.";
		this.luck = new GameStateIntegerConfig( "Luck", luckComment, 3, 4, 5, 1, 100 );

		this.group.addConfig( this.luck );
	}

	@SubscribeEvent
	public static void increaseLuck( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;

		LUCK_ATTRIBUTE.setValueAndApply( player, Instances.FISHERMAN_EMBLEM_ITEM.getEmblemLuckBonus( player ) );
	}

	/** Returns current luck bonus. (whether player has emblem or not) */
	public int getEmblemLuckBonus( PlayerEntity player ) {
		return player.fishingBobber != null && hasAny( player, this ) ? this.luck.getCurrentGameStateValue() : 0;
	}
}

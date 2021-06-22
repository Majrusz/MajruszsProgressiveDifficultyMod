package com.majruszs_difficulty.triggers;

import com.google.gson.JsonObject;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

/** Trigger called when the player starts to bleeding from a cactus. */
public class CactusBleedingTrigger extends AbstractCriterionTrigger< CactusBleedingTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "cactus_bleeding" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public CactusBleedingTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate entityPredicate,
		ConditionArrayParser conditionsParser
	) {
		return new CactusBleedingTrigger.Instance( entityPredicate );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player ) {
		this.triggerListeners( player, instance->true );
	}

	public static class Instance extends CriterionInstance {
		public Instance( EntityPredicate.AndPredicate player ) {
			super( CactusBleedingTrigger.ID, player );
		}
	}
}

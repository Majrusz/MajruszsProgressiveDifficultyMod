package com.majruszs_difficulty.triggers;

import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

/** Trigger called when Enderman teleports the target it attacks. */
public class EndermanTeleportAttackTrigger extends AbstractCriterionTrigger< EndermanTeleportAttackTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "enderman_teleport_attack" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public EndermanTeleportAttackTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate predicate,
		ConditionArrayParser conditions
	) {
		return new EndermanTeleportAttackTrigger.Instance( predicate );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player ) {
		this.triggerListeners( player, instance->true );
	}

	public static class Instance extends CriterionInstance {
		public Instance( EntityPredicate.AndPredicate predicate ) {
			super( EndermanTeleportAttackTrigger.ID, predicate );
		}
	}
}

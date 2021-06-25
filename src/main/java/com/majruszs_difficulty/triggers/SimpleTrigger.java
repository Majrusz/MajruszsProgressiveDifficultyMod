package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/** Trigger called when player do certain action without any parameters. */
public class SimpleTrigger extends AbstractCriterionTrigger< SimpleTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "simple_trigger" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public SimpleTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate predicate, ConditionArrayParser conditions
	) {
		JsonElement triggerType = jsonObject.get( "type" );

		return new SimpleTrigger.Instance( predicate, triggerType.getAsString() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player, String triggerType ) {
		this.triggerListeners( player, instance->instance.test( triggerType ) );
	}

	public static class Instance extends CriterionInstance {
		private final String triggerType;

		public Instance( EntityPredicate.AndPredicate predicate, String triggerType ) {
			super( SimpleTrigger.ID, predicate );

			this.triggerType = triggerType;
		}

		public JsonObject serialize( ConditionArraySerializer conditions ) {
			JsonObject jsonObject = super.serialize( conditions );
			jsonObject.addProperty( "type", this.triggerType );

			return jsonObject;
		}

		public boolean test( String effectType ) {
			return this.triggerType.equals( effectType );
		}
	}
}

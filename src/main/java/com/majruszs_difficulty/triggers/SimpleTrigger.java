package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Trigger called when player do certain action without any parameters. */
public class SimpleTrigger extends SimpleCriterionTrigger< SimpleTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "simple_trigger" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public SimpleTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext conditions
	) {
		JsonElement triggerType = jsonObject.get( "type" );

		return new SimpleTrigger.Instance( predicate, triggerType.getAsString() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, String triggerType ) {
		this.trigger( player, instance->instance.test( triggerType ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final String triggerType;

		public Instance( EntityPredicate.Composite predicate, String triggerType ) {
			super( SimpleTrigger.ID, predicate );

			this.triggerType = triggerType;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "type", this.triggerType );

			return jsonObject;
		}

		public boolean test( String effectType ) {
			return this.triggerType.equals( effectType );
		}
	}
}

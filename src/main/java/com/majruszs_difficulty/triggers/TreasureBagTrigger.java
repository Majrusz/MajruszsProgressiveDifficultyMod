package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.items.TreasureBagItem;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Trigger called when player opens a treasure bag. */
public class TreasureBagTrigger extends SimpleCriterionTrigger< TreasureBagTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "treasure_bag_opened" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public TreasureBagTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext conditions
	) {
		JsonElement bagID = jsonObject.get( "bag_id" );
		JsonElement amountOfBags = jsonObject.get( "amount" );
		return new TreasureBagTrigger.Instance( predicate, bagID.getAsString(), amountOfBags.getAsInt() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, TreasureBagItem item, int amountOfBags ) {
		this.trigger( player, instance->instance.test( item, amountOfBags ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final String bagID;
		private final int amountOfBags;

		public Instance( EntityPredicate.Composite predicate, String bagID, int amountOfBags ) {
			super( TreasureBagTrigger.ID, predicate );

			this.bagID = bagID;
			this.amountOfBags = amountOfBags;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "bag_id", this.bagID );
			jsonObject.addProperty( "amount", this.amountOfBags );

			return jsonObject;
		}

		public boolean test( TreasureBagItem item, int amountOfBags ) {
			return item.getRegistryName() != null && amountOfBags >= this.amountOfBags && item.getRegistryName()
				.toString()
				.equals( this.bagID );
		}
	}
}

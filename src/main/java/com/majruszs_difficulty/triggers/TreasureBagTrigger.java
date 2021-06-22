package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.items.TreasureBagItem;
import com.mlib.MajruszLibrary;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/** Trigger called when player opens a treasure bag. */
public class TreasureBagTrigger extends AbstractCriterionTrigger< TreasureBagTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "treasure_bag_opened" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public TreasureBagTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate predicate,
		ConditionArrayParser conditions
	) {
		JsonElement bagID = jsonObject.get( "bag_id" );
		JsonElement amountOfBags = jsonObject.get( "amount" );
		return new TreasureBagTrigger.Instance( predicate, bagID.getAsString(), amountOfBags.getAsInt() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player, TreasureBagItem item, int amountOfBags ) {
		this.triggerListeners( player, instance->instance.test( item, amountOfBags ) );
	}

	public static class Instance extends CriterionInstance {
		private final String bagID;
		private final int amountOfBags;

		public Instance( EntityPredicate.AndPredicate predicate, String bagID, int amountOfBags ) {
			super( TreasureBagTrigger.ID, predicate );

			this.bagID = bagID;
			this.amountOfBags = amountOfBags;
		}

		public JsonObject serialize( ConditionArraySerializer conditions ) {
			JsonObject jsonObject = super.serialize( conditions );
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

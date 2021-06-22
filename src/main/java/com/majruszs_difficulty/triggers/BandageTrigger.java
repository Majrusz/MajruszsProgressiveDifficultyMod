package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.items.BandageItem;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/** Trigger called when player uses any Bandage on himself or on someone else. */
public class BandageTrigger extends AbstractCriterionTrigger< BandageTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "bandage_used" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public BandageTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate predicate, ConditionArrayParser conditions
	) {
		JsonElement bandageID = jsonObject.get( "bandage_id" );
		JsonElement usedOnOneself = jsonObject.get( "used_on_oneself" );

		return new BandageTrigger.Instance( predicate, bandageID.getAsString(), usedOnOneself.getAsBoolean() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player, BandageItem item, boolean usedOnOneself ) {
		this.triggerListeners( player, instance->instance.test( item, usedOnOneself ) );
	}

	public static class Instance extends CriterionInstance {
		private final String bandageID;
		private final boolean usedOnOneself;

		public Instance( EntityPredicate.AndPredicate predicate, String bandageID, boolean usedOnOneself ) {
			super( BandageTrigger.ID, predicate );

			this.bandageID = bandageID;
			this.usedOnOneself = usedOnOneself;
		}

		public JsonObject serialize( ConditionArraySerializer conditions ) {
			JsonObject jsonObject = super.serialize( conditions );
			jsonObject.addProperty( "bandage_id", this.bandageID );
			jsonObject.addProperty( "used_on_oneself", this.usedOnOneself );

			return jsonObject;
		}

		public boolean test( BandageItem item, boolean usedOnOneself ) {
			return item.getRegistryName() != null && usedOnOneself == this.usedOnOneself && item.getRegistryName()
				.toString()
				.equals( this.bandageID );
		}
	}
}

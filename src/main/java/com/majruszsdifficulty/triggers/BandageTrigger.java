package com.majruszsdifficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.BandageItem;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Trigger called when player uses any Bandage on himself or on someone else. */
public class BandageTrigger extends SimpleCriterionTrigger< BandageTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "bandage_used" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public BandageTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext conditions
	) {
		JsonElement bandageID = jsonObject.get( "bandage_id" );
		JsonElement usedOnOneself = jsonObject.get( "used_on_oneself" );

		return new BandageTrigger.Instance( predicate, bandageID.getAsString(), usedOnOneself.getAsBoolean() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, BandageItem item, boolean usedOnOneself ) {
		this.trigger( player, instance->instance.test( item, usedOnOneself ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final String bandageID;
		private final boolean usedOnOneself;

		public Instance( EntityPredicate.Composite predicate, String bandageID, boolean usedOnOneself ) {
			super( BandageTrigger.ID, predicate );

			this.bandageID = bandageID;
			this.usedOnOneself = usedOnOneself;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "bandage_id", this.bandageID );
			jsonObject.addProperty( "used_on_oneself", this.usedOnOneself );

			return jsonObject;
		}

		public boolean test( BandageItem item, boolean usedOnOneself ) {
			return usedOnOneself == this.usedOnOneself && Registry.ITEM.getKey( item )
				.toString()
				.equals( this.bandageID );
		}
	}
}

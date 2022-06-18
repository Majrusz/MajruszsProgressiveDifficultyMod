package com.majruszsdifficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Trigger called when player defeats a Undead Army. */
public class UndeadArmyDefeatedTrigger extends SimpleCriterionTrigger< UndeadArmyDefeatedTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "undead_army_defeated" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public UndeadArmyDefeatedTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate,
		DeserializationContext conditions
	) {
		JsonElement amountOfBags = jsonObject.get( "wave" );

		return new UndeadArmyDefeatedTrigger.Instance( predicate, amountOfBags.getAsInt() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, int wave ) {
		this.trigger( player, instance->instance.test( wave ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final int wave;

		public Instance( EntityPredicate.Composite predicate, int wave ) {
			super( UndeadArmyDefeatedTrigger.ID, predicate );

			this.wave = wave;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "wave", this.wave );

			return jsonObject;
		}

		public boolean test( int wave ) {
			return wave >= this.wave;
		}
	}
}

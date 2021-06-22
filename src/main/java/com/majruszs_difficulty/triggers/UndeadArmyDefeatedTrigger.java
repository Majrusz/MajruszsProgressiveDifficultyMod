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

/** Trigger called when player defeats a Undead Army. */
public class UndeadArmyDefeatedTrigger extends AbstractCriterionTrigger< UndeadArmyDefeatedTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "undead_army_defeated" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public UndeadArmyDefeatedTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate predicate,
		ConditionArrayParser conditions
	) {
		JsonElement amountOfBags = jsonObject.get( "wave" );

		return new UndeadArmyDefeatedTrigger.Instance( predicate, amountOfBags.getAsInt() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayerEntity player, int wave ) {
		this.triggerListeners( player, instance->instance.test( wave ) );
	}

	public static class Instance extends CriterionInstance {
		private final int wave;

		public Instance( EntityPredicate.AndPredicate predicate, int wave ) {
			super( UndeadArmyDefeatedTrigger.ID, predicate );

			this.wave = wave;
		}

		public JsonObject serialize( ConditionArraySerializer conditions ) {
			JsonObject jsonObject = super.serialize( conditions );
			jsonObject.addProperty( "wave", this.wave );

			return jsonObject;
		}

		public boolean test( int wave ) {
			return wave >= this.wave;
		}
	}
}

package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/** Trigger called when given game state is enabled. */
public class GameStateTrigger extends AbstractCriterionTrigger< GameStateTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "game_state" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public GameStateTrigger.Instance deserializeTrigger( JsonObject jsonObject, EntityPredicate.AndPredicate entityPredicate,
		ConditionArrayParser conditionsParser
	) {
		JsonElement stateElement = jsonObject.get( "state_id" );
		return new GameStateTrigger.Instance( entityPredicate, GameState.convertIntegerToState( stateElement.getAsInt() ) );
	}

	public void trigger( ServerPlayerEntity player, GameState.State state ) {
		this.triggerListeners( player, instance->instance.test( state ) );
	}

	public static class Instance extends CriterionInstance {
		private final GameState.State state;

		public Instance( EntityPredicate.AndPredicate player, GameState.State state ) {
			super( GameStateTrigger.ID, player );

			this.state = state;
		}

		public JsonObject serialize( ConditionArraySerializer conditions ) {
			JsonObject jsonObject = super.serialize( conditions );
			jsonObject.addProperty( "state_id", GameState.convertStateToInteger( this.state ) );

			return jsonObject;
		}

		public boolean test( GameState.State state ) {
			return GameState.convertStateToInteger( state ) >= GameState.convertStateToInteger( this.state );
		}
	}
}

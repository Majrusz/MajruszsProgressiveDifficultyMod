package com.majruszs_difficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Trigger called when given game state is enabled. */
@Mod.EventBusSubscriber
public class GameStateTrigger extends SimpleCriterionTrigger< GameStateTrigger.Instance > {
	private static final ResourceLocation ID = MajruszsDifficulty.getLocation( "game_state" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public GameStateTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext conditions
	) {
		JsonElement stateElement = jsonObject.get( "state_id" );
		return new GameStateTrigger.Instance( predicate, GameState.convertIntegerToState( stateElement.getAsInt() ) );
	}

	@SubscribeEvent
	public static void onStart( PlayerEvent.PlayerLoggedInEvent event ) {
		if( event.getPlayer() instanceof ServerPlayer )
			GameState.triggerAdvancement( ( ServerPlayer )event.getPlayer() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, GameState.State state ) {
		this.trigger( player, instance->instance.test( state ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final GameState.State state;

		public Instance( EntityPredicate.Composite player, GameState.State state ) {
			super( GameStateTrigger.ID, player );

			this.state = state;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "state_id", GameState.convertStateToInteger( this.state ) );

			return jsonObject;
		}

		public boolean test( GameState.State state ) {
			return GameState.convertStateToInteger( state ) >= GameState.convertStateToInteger( this.state );
		}
	}
}

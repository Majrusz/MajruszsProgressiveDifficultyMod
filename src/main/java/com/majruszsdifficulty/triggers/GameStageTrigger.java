package com.majruszsdifficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Trigger called when given game stage is enabled. */
@Mod.EventBusSubscriber
public class GameStageTrigger extends SimpleCriterionTrigger< GameStageTrigger.Instance > {
	private static final ResourceLocation ID = Registries.getLocation( "game_state" );

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public GameStageTrigger.Instance createInstance( JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext conditions
	) {
		JsonElement stateElement = jsonObject.get( "state_id" );
		return new GameStageTrigger.Instance( predicate, GameStage.convertIntegerToStage( stateElement.getAsInt() ) );
	}

	@SubscribeEvent
	public static void onStart( PlayerEvent.PlayerLoggedInEvent event ) {
		if( event.getEntity() instanceof ServerPlayer )
			GameStage.triggerAdvancement( ( ServerPlayer )event.getEntity() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, GameStage.Stage stage ) {
		this.trigger( player, instance->instance.test( stage ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final GameStage.Stage stage;

		public Instance( EntityPredicate.Composite player, GameStage.Stage stage ) {
			super( GameStageTrigger.ID, player );

			this.stage = stage;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "state_id", GameStage.convertStageToInteger( this.stage ) );

			return jsonObject;
		}

		public boolean test( GameStage.Stage stage ) {
			return GameStage.convertStageToInteger( stage ) >= GameStage.convertStageToInteger( this.stage );
		}
	}
}

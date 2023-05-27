package com.majruszsdifficulty.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.gamestage.GameStage;
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
			Registries.GAME_STATE_TRIGGER.trigger( ( ServerPlayer )event.getEntity(), GameStage.getCurrentStage() );
	}

	/** Triggers an advancement for given player. */
	public void trigger( ServerPlayer player, GameStage stage ) {
		this.trigger( player, instance->instance.test( stage ) );
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final GameStage stage;

		public Instance( EntityPredicate.Composite player, GameStage stage ) {
			super( GameStageTrigger.ID, player );

			this.stage = stage;
		}

		public JsonObject serializeToJson( SerializationContext conditions ) {
			JsonObject jsonObject = super.serializeToJson( conditions );
			jsonObject.addProperty( "state_id", this.stage.ordinal() );

			return jsonObject;
		}

		public boolean test( GameStage stage ) {
			return stage.ordinal() >= this.stage.ordinal();
		}
	}
}

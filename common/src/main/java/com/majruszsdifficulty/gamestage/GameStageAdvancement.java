package com.majruszsdifficulty.gamestage;

import com.google.gson.JsonObject;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GameStageAdvancement extends SimpleCriterionTrigger< GameStageAdvancement.Instance > {
	final ResourceLocation id = MajruszsDifficulty.HELPER.getLocation( "game_stage" );

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public Instance createInstance( JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context ) {
		return Serializables.read( new Instance( this.id, predicate ), json );
	}

	public void trigger( ServerPlayer player, GameStage gameStage ) {
		this.trigger( player, instance->GameStageHelper.find( instance.stageId ).getOrdinal() <= gameStage.getOrdinal() );
	}

	static class Instance extends AbstractCriterionTriggerInstance {
		String stageId;

		static {
			Serializables.get( Instance.class )
				.define( "stage_id", Reader.string(), s->s.stageId, ( s, v )->s.stageId = v );
		}

		public Instance( ResourceLocation id, EntityPredicate.Composite predicate ) {
			super( id, predicate );
		}

		@Override
		public JsonObject serializeToJson( SerializationContext conditions ) {
			return Serializables.write( this, super.serializeToJson( conditions ) ).getAsJsonObject();
		}
	}
}

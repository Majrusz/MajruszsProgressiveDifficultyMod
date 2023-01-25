package com.majruszsdifficulty.undeadarmy;

import com.google.gson.*;
import com.majruszsdifficulty.Registries;
import com.mlib.json.JsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceListener extends SimpleJsonResourceReloadListener {
	static final Gson GSON = Deserializers.createFunctionSerializer().registerTypeAdapter( WaveConfigs.class, new Serializer() ).create();
	WaveConfigs waveConfigs = null;

	public ResourceListener() {
		super( GSON, "undead_army" );
	}

	public int getWavesNum() {
		return this.waveConfigs.list.size();
	}

	@Override
	protected void apply( Map< ResourceLocation, JsonElement > elements, ResourceManager manager, ProfilerFiller filler ) {
		this.waveConfigs = GSON.fromJson( elements.get( Registries.getLocation( "waves" ) ), WaveConfigs.class );
	}

	static class WaveConfig {
		final List< Mob > mobs = new ArrayList<>();
		Mob boss = null;

		public WaveConfig( JsonObject object ) {
			this.addMobs( object );
			this.tryToAddBoss( object );
		}

		private void addMobs( JsonObject object ) {
			object.getAsJsonArray( "mobs" )
				.forEach( mobElement->{
					JsonObject mobObject = mobElement.getAsJsonObject();
					Mob mob = new Mob( mobObject );
					int count = JsonHelper.getAsInt( mobObject, "count", 1 );
					for( int i = 0; i < count; ++i ) {
						this.mobs.add( mob );
					}
				} );
		}

		private void tryToAddBoss( JsonObject object ) {
			if( object.has( "boss" ) ) {
				this.boss = new Mob( object.getAsJsonObject( "boss" ) );
			}
		}

		record Mob( EntityType< ? > entityType, ResourceLocation equipmentLocation ) {
			public Mob( JsonObject object ) {
				this(
					JsonHelper.getAsEntity( object, "id" ),
					JsonHelper.getAsLocation( object, "equipment", LootTable.EMPTY.getLootTableId() )
				);
			}
		}
	}

	record WaveConfigs( List< WaveConfig > list ) {}

	static class Serializer implements JsonDeserializer< WaveConfigs > {
		@Override
		public WaveConfigs deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException {
			List< WaveConfig > wavesInfo = new ArrayList<>();
			element.getAsJsonArray().forEach( waveElement->wavesInfo.add( new WaveConfig( waveElement.getAsJsonObject() ) ) );

			return new WaveConfigs( wavesInfo );
		}
	}
}

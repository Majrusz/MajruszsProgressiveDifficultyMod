package com.majruszsdifficulty.undeadarmy;

import com.google.gson.*;
import com.majruszsdifficulty.Registries;
import com.mlib.data.Data;
import com.mlib.data.ISerializable;
import com.mlib.data.SerializableStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

class ResourceListener extends SimpleJsonResourceReloadListener {
	static final Gson GSON = Deserializers.createFunctionSerializer().registerTypeAdapter( Resource.class, new Serializer() ).create();
	Resource resource = null;

	public ResourceListener() {
		super( GSON, "undead_army" );
	}

	public int getWavesNum() {
		return this.resource.waves.get().size();
	}

	@Override
	protected void apply( Map< ResourceLocation, JsonElement > elements, ResourceManager manager, ProfilerFiller filler ) {
		this.resource = GSON.fromJson( elements.get( Registries.getLocation( "waves" ) ), Resource.class );
	}

	static class Resource extends SerializableStructure {
		final Data< List< Wave > > waves = this.addList( Wave::new );

		static class Wave extends SerializableStructure {
			final Data< List< Mob > > mobs = this.addList( Mob::new ).key( "mobs" );
			final Data< Mob > boss = this.addStructure( Mob::new ).key( "boss" );

			static class Mob extends SerializableStructure {
				final Data< EntityType< ? > > type = this.addEntityType().key( "id" );
				final Data< Integer > count = this.addInteger().key( "count" ).or( 1 );
				final Data< ResourceLocation > equipment = this.addResourceLocation().key( "equipment" ).or( LootTable.EMPTY.getLootTableId() );
			}
		}
	}

	static class Serializer implements JsonDeserializer< Resource > {
		@Override
		public Resource deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException {
			Resource structure = new Resource();
			structure.read( element );

			return structure;
		}
	}
}

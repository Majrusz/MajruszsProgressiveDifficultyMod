package com.majruszsdifficulty.undeadarmy.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mlib.data.SerializableStructure;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WavesDef extends SerializableStructure {
	public final List< WaveDef > waveDefs = new ArrayList<>();

	public WavesDef() {
		this.define( null, ()->this.waveDefs, this.waveDefs::addAll, WaveDef::new );
	}

	public List< WaveDef > get() {
		return this.waveDefs;
	}

	public static class Serializer implements JsonDeserializer< WavesDef > {
		@Override
		public WavesDef deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException {
			WavesDef wavesDef = new WavesDef();
			wavesDef.read( element );

			return wavesDef;
		}
	}
}

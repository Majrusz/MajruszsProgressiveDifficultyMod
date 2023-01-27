package com.majruszsdifficulty.undeadarmy;

import com.google.gson.*;
import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoInstance
public class Config extends GameModifier {
	static final Gson GSON = Deserializers.createFunctionSerializer().registerTypeAdapter( WavesDef.class, new WavesDef.Serializer() ).create();
	private final SimpleJsonResourceReloadListener resourceListener = new SimpleJsonResourceReloadListener( GSON, "undead_army" ) {
		@Override
		protected void apply( Map< ResourceLocation, JsonElement > elements, ResourceManager manager, ProfilerFiller filler ) {
			Config.this.wavesDef = GSON.fromJson( elements.get( Registries.getLocation( "waves" ) ), WavesDef.class );
		}
	};
	private WavesDef wavesDef = null;

	public Config() {
		super( Registries.Modifiers.UNDEAD_ARMY );

		MinecraftForge.EVENT_BUS.addListener( ( AddReloadListenerEvent event )->event.addListener( this.resourceListener ) );
	}

	public int getWavesNum() {
		return this.wavesDef.get().size();
	}

	public List< MobDef > getWaveMobs( int waveIdx ) {
		return this.getWave( waveIdx ).mobDefs;
	}

	public WaveDef getWave( int waveIdx ) {
		return this.wavesDef.get().get( Mth.clamp( waveIdx - 1, 0, this.getWavesNum() ) );
	}

	static class WavesDef extends SerializableStructure {
		final List< WaveDef > waveDefs = new ArrayList<>();

		public WavesDef() {
			this.define( null, ()->this.waveDefs, this.waveDefs::addAll, WaveDef::new );
		}

		public List< WaveDef > get() {
			return this.waveDefs;
		}

		static class Serializer implements JsonDeserializer< WavesDef > {
			@Override
			public WavesDef deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException {
				WavesDef wavesDef = new WavesDef();
				wavesDef.read( element );

				return wavesDef;
			}
		}
	}

	static class WaveDef extends SerializableStructure {
		final List< MobDef > mobDefs = new ArrayList<>();
		BossDef boss;

		public WaveDef() {
			this.define( "mobs", ()->this.mobDefs, this.mobDefs::addAll, MobDef::new );
			this.define( "boss", ()->this.boss, x->this.boss = x, BossDef::new );
		}
	}

	static class MobDef extends SerializableStructure {
		EntityType< ? > type;
		int count = 1;
		ResourceLocation equipment = LootTable.EMPTY.getLootTableId();
		boolean isBoss = false;

		public MobDef() {
			this.define( "id", ()->this.type, x->this.type = x );
			this.define( "count", ()->this.count, x->this.count = x );
			this.define( "equipment", ()->this.equipment, x->this.equipment = x );
		}
	}

	static class BossDef extends MobDef {
		public BossDef() {
			this.isBoss = true;
		}
	}
}

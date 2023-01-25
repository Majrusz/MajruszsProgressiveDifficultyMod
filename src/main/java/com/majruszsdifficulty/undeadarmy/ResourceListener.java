package com.majruszsdifficulty.undeadarmy;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.util.Map;

public class ResourceListener extends SimpleJsonResourceReloadListener {
	public ResourceListener() {
		super( Deserializers.createFunctionSerializer().create(), "undead_army" );
	}

	@Override
	protected void apply( Map< ResourceLocation, JsonElement > elements, ResourceManager manager, ProfilerFiller filler ) {
		int xd = 1;
	}
}

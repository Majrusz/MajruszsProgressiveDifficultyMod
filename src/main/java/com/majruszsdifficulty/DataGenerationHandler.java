package com.majruszsdifficulty;

import com.majruszsdifficulty.effects.BleedingEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, modid = MajruszsDifficulty.MOD_ID )
public class DataGenerationHandler {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
		.add( net.minecraft.core.registries.Registries.DAMAGE_TYPE, ( BootstapContext< DamageType > context )->{
			context.register( Registries.BLEEDING_SOURCE, new DamageType( "bleeding", 0.1f ) );
		} );

	@SubscribeEvent
	public static void onGatherData( GatherDataEvent event ) {
		DataGenerator generator = event.getGenerator();
		CompletableFuture< HolderLookup.Provider > lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();
		PackOutput output = generator.getPackOutput();
		BlockTagsProvider blockTagsProvider = new BlockTagsProvider( output, lookupProvider, MajruszsDifficulty.MOD_ID, helper ) {
			@Override
			protected void addTags( HolderLookup.Provider provider ) {}
		};

		generator.addProvider( event.includeServer(), new DatapackBuiltinEntriesProvider( output, lookupProvider, BUILDER, Set.of( MajruszsDifficulty.MOD_ID ) ) );
		generator.addProvider( event.includeServer(), new BleedingEffect.TagsProvider( output, lookupProvider.thenApply( DataGenerationHandler::append ), helper ) );
	}

	private static HolderLookup.Provider append( HolderLookup.Provider provider ) {
		return BUILDER.buildPatch( RegistryAccess.fromRegistryOfRegistries( BuiltInRegistries.REGISTRY ), provider );
	}
}

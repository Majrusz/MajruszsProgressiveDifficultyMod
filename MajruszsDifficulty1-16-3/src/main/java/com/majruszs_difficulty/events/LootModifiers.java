package com.majruszs_difficulty.events;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.loot_modifiers.IncreaseLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		event.getRegistry()
			.register( new IncreaseLoot.Serializer().setRegistryName( new ResourceLocation( MajruszsDifficulty.MOD_ID, "increase_loot" ) ) );
	}
}

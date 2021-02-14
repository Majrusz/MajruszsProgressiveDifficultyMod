package com.majruszs_difficulty.events;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.loot_modifiers.AddTreasureBagsToLoot;
import com.majruszs_difficulty.loot_modifiers.DisableCertainLoot;
import com.majruszs_difficulty.loot_modifiers.DoubleLoot;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		IForgeRegistry< GlobalLootModifierSerializer< ? > > registry = event.getRegistry();

		registry.register( new DoubleLoot.Serializer().setRegistryName( MajruszsDifficulty.getLocation( "double_loot" ) ) );
		registry.register( new AddTreasureBagsToLoot.Serializer().setRegistryName( MajruszsDifficulty.getLocation( "add_treasure_bag_to_loot" ) ) );
		registry.register( new DisableCertainLoot.Serializer().setRegistryName( MajruszsDifficulty.getLocation( "disable_certain_loot" ) ) );
	}
}

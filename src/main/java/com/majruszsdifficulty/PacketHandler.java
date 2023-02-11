package com.majruszsdifficulty;

import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.treasurebags.LootProgressManager;
import com.mlib.NetworkHandler;
import com.mlib.data.SerializableStructure;
import com.mlib.network.NetworkMessage;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Handling connection between server and client. */
public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( Registries.getLocation( "main" ), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals );
		SerializableStructure.register( CHANNEL, 0, TankEntity.Message.class, TankEntity.Message::new );
		CHANNEL.registerMessage( 1, CursedArmorEntity.AssembleMessage.class, CursedArmorEntity.AssembleMessage::encode, CursedArmorEntity.AssembleMessage::new, CursedArmorEntity.AssembleMessage::handle );
		CHANNEL.registerMessage( 2, LootProgressManager.ProgressMessage.class, LootProgressManager.ProgressMessage::encode, LootProgressManager.ProgressMessage::new, LootProgressManager.ProgressMessage::handle );
		CHANNEL.registerMessage( 3, BleedingEffect.BloodMessage.class, BleedingEffect.BloodMessage::encode, BleedingEffect.BloodMessage::new, BleedingEffect.BloodMessage::handle );
	}
}

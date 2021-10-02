package com.majruszs_difficulty;

import com.majruszs_difficulty.entities.TankEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

/** Handling connection between server and client. */
public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( MajruszsDifficulty.getLocation( "main" ), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
		);
		CHANNEL.registerMessage( 0, TankEntity.TankAttackMessage.class, TankEntity.TankAttackMessage::encode,
			TankEntity.TankAttackMessage::new, TankEntity.TankAttackMessage::handle
		);
	}
}

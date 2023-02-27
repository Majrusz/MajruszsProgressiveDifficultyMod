package com.majruszsdifficulty;

import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.entities.CerberusEntity;
import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.treasurebags.data.LootProgressData;
import com.mlib.data.SerializableStructure;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Handling connection between server and client. */
public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( Registries.getLocation( "main" ), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals );
		SerializableStructure.register( CHANNEL, 0, TankEntity.SkillMessage.class, TankEntity.SkillMessage::new );
		CHANNEL.registerMessage( 1, CursedArmorEntity.AssembleMessage.class, CursedArmorEntity.AssembleMessage::encode, CursedArmorEntity.AssembleMessage::new, CursedArmorEntity.AssembleMessage::handle );
		SerializableStructure.register( CHANNEL, 2, LootProgressData.class, LootProgressData::new );
		CHANNEL.registerMessage( 3, BleedingEffect.BloodMessage.class, BleedingEffect.BloodMessage::encode, BleedingEffect.BloodMessage::new, BleedingEffect.BloodMessage::handle );
		SerializableStructure.register( CHANNEL, 4, CerberusEntity.SkillMessage.class, CerberusEntity.SkillMessage::new );
		SerializableStructure.register( CHANNEL, 5, CerberusEntity.TargetMessage.class, CerberusEntity.TargetMessage::new );
	}
}

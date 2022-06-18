package com.majruszsdifficulty.particles;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@SubscribeEvent
	public static void registerParticles( ParticleFactoryRegisterEvent event ) {
		Minecraft.getInstance().particleEngine.register( Instances.BLOOD_PARTICLE, BloodParticle.Factory::new );
	}
}

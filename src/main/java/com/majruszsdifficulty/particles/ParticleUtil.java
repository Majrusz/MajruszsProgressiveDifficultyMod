package com.majruszsdifficulty.particles;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void registerParticles( RegisterParticleProvidersEvent event ) {
		event.registerSpriteSet( Registries.BLOOD.get(), BloodParticle.Factory::new );
	}
}

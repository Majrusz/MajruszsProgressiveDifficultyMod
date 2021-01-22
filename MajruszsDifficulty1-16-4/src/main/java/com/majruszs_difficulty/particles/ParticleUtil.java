package com.majruszs_difficulty.particles;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@SubscribeEvent
	public static void registerParticles( ParticleFactoryRegisterEvent event ) {
		Minecraft.getInstance().particles.registerFactory( Instances.BLOOD_PARTICLE, BloodParticle.Factory::new );
	}
}

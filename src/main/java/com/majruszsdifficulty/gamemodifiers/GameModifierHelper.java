package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.effects.EffectHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class GameModifierHelper {
	public static void applyBleeding( DamagedContext.Data data, Config.Bleeding config ) {
		LivingEntity target = data.target;
		@Nullable LivingEntity attacker = data.attacker;
		BleedingEffect.MobEffectInstance effectInstance = new BleedingEffect.MobEffectInstance( config.getDuration(), config.getAmplifier(), false, true, attacker );
		EffectHelper.applyEffectIfPossible( target, effectInstance );

		if( target instanceof ServerPlayer targetPlayer ) {
			Registries.BASIC_TRIGGER.trigger( targetPlayer, "bleeding_received" );
			if( data.source.equals( DamageSource.CACTUS ) ) {
				Registries.BASIC_TRIGGER.trigger( targetPlayer, "cactus_bleeding" );
			}
		}
		if( attacker instanceof ServerPlayer attackerPlayer ) {
			Registries.BASIC_TRIGGER.trigger( attackerPlayer, "bleeding_inflicted" );
		}
	}
}

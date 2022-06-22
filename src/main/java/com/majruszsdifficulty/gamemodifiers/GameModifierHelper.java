package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class GameModifierHelper {
	public static void applyBleeding( LivingEntity target, @Nullable LivingEntity attacker, Config.Bleeding config ) {
		BleedingEffect.MobEffectInstance effectInstance = new BleedingEffect.MobEffectInstance( config.getDuration(), config.getAmplifier(), false, true, attacker );
		EffectHelper.applyEffectIfPossible( target, effectInstance );

		if( target instanceof ServerPlayer targetPlayer ) {
			Registries.BASIC_TRIGGER.trigger( targetPlayer, "bleeding_received" );
		} else if( attacker instanceof ServerPlayer attackerPlayer ) {
			Registries.BASIC_TRIGGER.trigger( attackerPlayer, "bleeding_inflicted" );
		}
	}
}

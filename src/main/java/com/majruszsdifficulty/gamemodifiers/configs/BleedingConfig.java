package com.majruszsdifficulty.gamemodifiers.configs;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class BleedingConfig extends ProgressiveEffectConfig {
	public BleedingConfig( GameStage.Double duration ) {
		super( Registries.BLEEDING, new GameStage.Integer( 0, 1, 2 ), duration );
	}

	public BleedingConfig( double duration ) {
		this( new GameStage.Double( duration, duration, duration ) );
	}

	public BleedingConfig() {
		this( 24.0 );
	}

	public boolean apply( OnDamaged.Data data ) {
		LivingEntity target = data.target;
		@Nullable LivingEntity attacker = data.attacker;
		BleedingEffect.MobEffectInstance effectInstance = new BleedingEffect.MobEffectInstance( getDuration(), getAmplifier(), false, attacker );
		if( !MobEffectHelper.tryToApply( target, effectInstance ) )
			return false;

		if( target instanceof ServerPlayer targetPlayer ) {
			Registries.BASIC_TRIGGER.trigger( targetPlayer, "bleeding_received" );
			if( data.source.equals( DamageSource.CACTUS ) ) {
				Registries.BASIC_TRIGGER.trigger( targetPlayer, "cactus_bleeding" );
			}
		}
		if( attacker instanceof ServerPlayer attackerPlayer ) {
			Registries.BASIC_TRIGGER.trigger( attackerPlayer, "bleeding_inflicted" );
		}

		return true;
	}
}

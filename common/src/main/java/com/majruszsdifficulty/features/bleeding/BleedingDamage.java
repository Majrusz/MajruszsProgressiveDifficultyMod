package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityDamaged;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Contexts;
import com.mlib.entity.EffectHelper;
import com.mlib.entity.EntityHelper;
import com.mlib.math.Random;
import com.mlib.time.TimeHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

@AutoInstance
public class BleedingDamage {
	static final int DAMAGE_COOLDOWN = TimeHelper.toTicks( 4.0 );
	final Map< Integer, Integer > entityTicks = new HashMap<>();

	public BleedingDamage() {
		OnEntityDamaged.listen( this::tryToApply )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( BleedingEffect::isEnabled )
			.addCondition( data->!BleedingEffect.isImmune( data.target ) );

		OnEntityTicked.listen( this::tick )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING, data.entity ) );
	}

	private void tryToApply( OnEntityDamaged data ) {
		if( Contexts.dispatch( new OnBleedingCheck( data ) ).isBleedingTriggered() && BleedingEffect.apply( data.target, data.attacker ) ) {
			this.dealDamage( data.target );
		}
	}

	private void tick( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.BLEEDING, data.entity ).orElse( 0 );
		int extraDuration = Random.round( 0.3 * ( amplifier + 2 ) * ( 7.26 * EntityHelper.getWalkDistanceDelta( data.entity ) + 1 ) );
		int duration = this.entityTicks.getOrDefault( data.entity.getId(), 0 ) + extraDuration;
		if( duration > DAMAGE_COOLDOWN ) {
			this.dealDamage( data.entity );
			duration = 0;
		}

		this.entityTicks.put( data.entity.getId(), duration );
	}

	private void dealDamage( LivingEntity entity ) {
		Holder< DamageType > damageType = entity.level()
			.registryAccess()
			.registryOrThrow( net.minecraft.core.registries.Registries.DAMAGE_TYPE )
			.getHolderOrThrow( MajruszsDifficulty.BLEEDING_SOURCE );

		if( entity.getEffect( MajruszsDifficulty.BLEEDING.get() ) instanceof BleedingEffect.MobEffectInstance effectInstance ) {
			Vec3 motion = entity.getDeltaMovement();
			entity.hurt( new DamageSource( damageType, null, effectInstance.damageSourceEntity ), 1.0f );
			entity.setDeltaMovement( motion ); // sets previous motion to avoid any knockback from bleeding
		} else {
			entity.hurt( new DamageSource( damageType ), 1.0f );
		}
	}
}

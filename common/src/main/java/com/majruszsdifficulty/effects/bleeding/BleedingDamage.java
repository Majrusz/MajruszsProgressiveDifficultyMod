package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.events.OnBleedingCheck;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class BleedingDamage {
	static final int DAMAGE_COOLDOWN = TimeHelper.toTicks( 4.0 );
	static final Map< Integer, Integer > ENTITY_TICKS = new HashMap<>();

	static {
		OnEntityDamaged.listen( BleedingDamage::tryToApply )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Bleeding::isEnabled )
			.addCondition( data->Bleeding.canApplyTo( data.target ) );

		OnEntityTicked.listen( BleedingDamage::tick )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING_EFFECT, data.entity ) );
	}

	private static void tryToApply( OnEntityDamaged data ) {
		if( Events.dispatch( new OnBleedingCheck( data ) ).isBleedingTriggered() && Bleeding.apply( data.target, data.attacker ) ) {
			BleedingDamage.dealDamage( data.target );
			BleedingDamage.giveAdvancements( data );
		}
	}

	private static void tick( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.BLEEDING_EFFECT, data.entity ).orElse( 0 );
		int extraDuration = Random.round( 0.3 * ( amplifier + 2 ) * ( 7.26 * EntityHelper.getWalkDistanceDelta( data.entity ) + 1 ) );
		int duration = ENTITY_TICKS.getOrDefault( data.entity.getId(), 0 ) + extraDuration;
		if( duration > DAMAGE_COOLDOWN ) {
			BleedingDamage.dealDamage( data.entity );
			duration = 0;
		}

		ENTITY_TICKS.put( data.entity.getId(), duration );
	}

	private static void dealDamage( LivingEntity entity ) {
		Holder< DamageType > damageType = entity.getLevel()
			.registryAccess()
			.registryOrThrow( net.minecraft.core.registries.Registries.DAMAGE_TYPE )
			.getHolderOrThrow( MajruszsDifficulty.BLEEDING_DAMAGE_SOURCE );

		if( entity.getEffect( MajruszsDifficulty.BLEEDING_EFFECT.get() ) instanceof Bleeding.MobEffectInstance effectInstance ) {
			Vec3 motion = entity.getDeltaMovement();
			entity.hurt( new DamageSource( damageType, null, effectInstance.damageSourceEntity ), 1.0f );
			entity.setDeltaMovement( motion ); // sets previous motion to avoid any knockback from bleeding
		} else {
			entity.hurt( new DamageSource( damageType ), 1.0f );
		}
	}

	private static void giveAdvancements( OnEntityDamaged data ) {
		if( data.target instanceof ServerPlayer player ) {
			MajruszsDifficulty.HELPER.triggerAchievement( player, "bleeding_received" );
			if( data.source.is( DamageTypes.CACTUS ) ) {
				MajruszsDifficulty.HELPER.triggerAchievement( player, "cactus_bleeding" );
			}
		}

		if( data.attacker instanceof ServerPlayer player ) {
			MajruszsDifficulty.HELPER.triggerAchievement( player, "bleeding_inflicted" );
		}
	}
}

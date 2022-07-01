package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** A bleeding effect similar to the poison effect. */
@Mod.EventBusSubscriber
public class BleedingEffect extends MobEffect {
	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {
		if( entity.getEffect( this ) instanceof MobEffectInstance effectInstance ) {
			Vec3 motion = entity.getDeltaMovement();
			entity.hurt( new EntityBleedingDamageSource( effectInstance.damageSourceEntity ), 1.0f );
			entity.setDeltaMovement( motion ); // sets previous motion to avoid any jumping from bleeding
		} else {
			entity.hurt( Registries.BLEEDING_SOURCE, 1.0f );
		}
	}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = Math.max( 4, Utility.secondsToTicks( 4.0 ) >> amplifier );

		return duration % cooldown == 0;
	}

	@Override
	public List< ItemStack > getCurativeItems() {
		return new ArrayList<>(); // removes the default milk bucket from curative items
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		BleedingEffect bleeding = Registries.BLEEDING.get();
		LivingEntity entity = event.getEntityLiving();
		if( TimeHelper.hasServerTicksPassed( 5 ) && entity.hasEffect( bleeding ) )
			spawnParticles( entity, EffectHelper.getEffectAmplifier( entity, bleeding ) + 3 );
	}

	@SubscribeEvent
	public static void onDeath( LivingDeathEvent event ) {
		if( event.getEntityLiving().hasEffect( Registries.BLEEDING.get() ) )
			spawnParticles( event.getEntityLiving(), 100 );
	}

	public static boolean isBleedingSource( DamageSource damageSource ) {
		return damageSource.msgId.equals( Registries.BLEEDING_SOURCE.msgId );
	}

	private static void spawnParticles( LivingEntity entity, int amountOfParticles ) {
		if( entity.level instanceof ServerLevel level )
			level.sendParticles( Registries.BLOOD.get(), entity.getX(), entity.getY( 0.5 ), entity.getZ(), amountOfParticles, 0.125, 0.5, 0.125, 0.05 );
	}

	/** Bleeding damage source that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class EntityBleedingDamageSource extends DamageSource {
		@Nullable protected final Entity damageSourceEntity;

		public EntityBleedingDamageSource( @Nullable Entity damageSourceEntity ) {
			super( Registries.BLEEDING_SOURCE.msgId );
			bypassArmor();

			this.damageSourceEntity = damageSourceEntity;
		}

		@Nullable
		@Override
		public Entity getDirectEntity() {
			return null;
		}

		@Nullable
		@Override
		public Entity getEntity() {
			return this.damageSourceEntity;
		}
	}

	/** Bleeding effect instance that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class MobEffectInstance extends net.minecraft.world.effect.MobEffectInstance {
		@Nullable protected final Entity damageSourceEntity;

		public MobEffectInstance( int duration, int amplifier, boolean ambient, boolean showParticles, @Nullable LivingEntity attacker ) {
			super( Registries.BLEEDING.get(), duration, amplifier, ambient, showParticles );
			this.damageSourceEntity = attacker;
		}
	}
}

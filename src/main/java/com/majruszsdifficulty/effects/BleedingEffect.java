package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.EffectHelper;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BleedingEffect extends MobEffect {
	public static final ParticleHandler PARTICLES = new ParticleHandler( Registries.BLOOD, ()->new Vec3( 0.125, 0.5, 0.125 ), ParticleHandler.speed( 0.05f ) );

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
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity,
		int amplifier, double health
	) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = Math.max( 4, Utility.secondsToTicks( 4.0 ) >> amplifier );

		return duration % cooldown == 0;
	}

	@Override
	public List< ItemStack > getCurativeItems() {
		return new ArrayList<>(); // removes the default milk bucket from curative items
	}

	/** Bleeding damage source that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class EntityBleedingDamageSource extends DamageSource {
		@Nullable protected final Entity damageSourceEntity;

		public EntityBleedingDamageSource( @Nullable Entity damageSourceEntity ) {
			super( Registries.BLEEDING_SOURCE.msgId );
			this.bypassArmor();

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

		public MobEffectInstance( int duration, int amplifier, boolean ambient, @Nullable LivingEntity attacker ) {
			super( Registries.BLEEDING.get(), duration, amplifier, ambient, false, true );
			this.damageSourceEntity = attacker;
		}
	}

	@AutoInstance
	public static class Bleeding extends GameModifier {
		public Bleeding() {
			super( Registries.Modifiers.DEFAULT, "Bleeding", "Common config for all Bleeding effects." );

			OnEntityTick.Context onTick = new OnEntityTick.Context( this::spawnParticles );
			onTick.addCondition( new Condition.IsServer() )
				.addCondition( new Condition.Cooldown( 5, Dist.DEDICATED_SERVER, false ) )
				.addCondition( new Condition.HasEffect( Registries.BLEEDING ) );

			OnDeath.Context onDeath = new OnDeath.Context( this::spawnParticles );
			onDeath.addCondition( new Condition.IsServer() ).addCondition( new Condition.HasEffect( Registries.BLEEDING ) );

			this.addContexts( onTick, onDeath );
		}

		private void spawnParticles( OnEntityTick.Data data ) {
			assert data.entity != null;

			this.spawnParticles( data.level, data.entity, MobEffectHelper.getEffectAmplifier( data.entity, Registries.BLEEDING.get() ) + 3 );
		}

		private void spawnParticles( OnDeath.Data data ) {
			assert data.entity != null;

			this.spawnParticles( data.level, data.entity, 100 );
		}

		private void spawnParticles( ServerLevel level, LivingEntity entity, int amountOfParticles ) {
			Vec3 position = new Vec3( entity.getX(), entity.getY( 0.5 ), entity.getZ() );
			PARTICLES.spawn( level, position, amountOfParticles );
		}
	}
}

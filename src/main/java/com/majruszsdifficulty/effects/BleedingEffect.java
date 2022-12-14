package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gui.BleedingGui;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnEffectApplicable;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;

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

		DistExecutor.unsafeRunWhenOn( Dist.CLIENT, ()->()->{
			if( entity == Minecraft.getInstance().player ) {
				BleedingGui.addBloodOnScreen();
			}
		} );
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
		final BooleanConfig effectConfig = new BooleanConfig( "is_enabled", "Specifies whether Bleeding should always be applicable.", false, true );
		final StringListConfig immuneMobs = new StringListConfig( "immune_mobs", "Specifies which mobs should not be affected by Bleeding (all undead mobs are immune by default).", false, "minecraft:skeleton_horse" );

		public Bleeding() {
			super( Registries.Modifiers.DEFAULT, "Bleeding", "Common config for all Bleeding effects." );

			OnEntityTick.Context onTick = new OnEntityTick.Context( this::spawnParticles );
			onTick.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 5, Dist.DEDICATED_SERVER ).setConfigurable( false ) )
				.addCondition( new Condition.HasEffect<>( Registries.BLEEDING ) )
				.addCondition( data->data.entity instanceof LivingEntity );

			OnDeath.Context onDeath = new OnDeath.Context( this::spawnParticles );
			onDeath.addCondition( new Condition.IsServer<>() ).addCondition( new Condition.HasEffect<>( Registries.BLEEDING ) );

			OnEffectApplicable.Context onEffectApplicable = new OnEffectApplicable.Context( this::cancelBleeding );
			onEffectApplicable.addCondition( new Condition.Excludable< OnEffectApplicable.Data >( this.effectConfig ).negate() )
				.addCondition( data->data.effect.equals( Registries.BLEEDING.get() ) || data.effect.equals( Registries.BLEEDING_IMMUNITY.get() ) );

			OnEffectApplicable.Context onEffectApplicable2 = new OnEffectApplicable.Context( this::cancelBleeding );
			onEffectApplicable2.addCondition( this::isImmune ).addConfig( this.immuneMobs );

			this.addContexts( onTick, onDeath, onEffectApplicable, onEffectApplicable2 );
		}

		private void spawnParticles( OnEntityTick.Data data ) {
			this.spawnParticles( data.level, data.entity, MobEffectHelper.getAmplifier( ( LivingEntity )data.entity, Registries.BLEEDING.get() ) + 3 );
		}

		private void spawnParticles( OnDeath.Data data ) {
			assert data.entity != null;

			this.spawnParticles( data.level, data.entity, 100 );
		}

		private void spawnParticles( ServerLevel level, Entity entity, int amountOfParticles ) {
			Vec3 position = new Vec3( entity.getX(), entity.getY( 0.5 ), entity.getZ() );
			PARTICLES.spawn( level, position, amountOfParticles );
		}

		private void cancelBleeding( OnEffectApplicable.Data data ) {
			data.event.setResult( Event.Result.DENY );
		}

		private boolean isImmune( OnEffectApplicable.Data data ) {
			assert data.entity != null;
			boolean isBleedingEffect = data.effect.equals( Registries.BLEEDING.get() );
			boolean isMobImmune = this.immuneMobs.contains( Utility.getRegistryString( data.entity.getType() ) );

			return isBleedingEffect && isMobImmune;
		}
	}
}

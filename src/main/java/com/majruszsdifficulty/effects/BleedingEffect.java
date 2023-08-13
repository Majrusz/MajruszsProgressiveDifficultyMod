package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.BleedingConfig;
import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.majruszsdifficulty.gui.BleedingGui;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.*;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BleedingEffect extends MobEffect {
	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	@Override
	public List< ItemStack > getCurativeItems() {
		return new ArrayList<>(); // removes milk bucket from the default curative items
	}

	/** Bleeding damage source that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class DamageSource extends net.minecraft.world.damagesource.DamageSource {
		@Nullable protected final Entity damageSourceEntity;

		public DamageSource( @Nullable Entity damageSourceEntity ) {
			super( Registries.BLEEDING_SOURCE.msgId );

			this.damageSourceEntity = damageSourceEntity;
			this.bypassArmor();
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
	public static class Bleeding {
		static final ParticleHandler PARTICLES = new ParticleHandler( Registries.BLOOD, ()->new Vec3( 0.2, 0.5, 0.2 ), ParticleHandler.speed( 0.075f ) );
		static final int BLOOD_TICK_COOLDOWN = Utility.secondsToTicks( 4.0 );
		final BooleanConfig availability = Condition.DefaultConfigs.excludable( true );
		final StringListConfig immuneMobs = new StringListConfig( "minecraft:skeleton_horse", "minecraft:zombie_horse" );
		final BleedingConfig effect = new BleedingConfig();
		final HashMap< Integer, Integer > entityTicks = new HashMap<>();

		public Bleeding() {
			ConfigGroup group = ModConfigs.getGroup( Registries.Groups.BLEEDING )
				.addConfig( this.availability )
				.addConfig( this.immuneMobs
					.name( "immune_mobs" )
					.comment( "Specifies which mobs should not be affected by Bleeding (all undead mobs are immune by default)." )
				).addConfig( this.effect );

			OnEntityTick.listen( this::spawnParticles )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.< OnEntityTick.Data > cooldown( 3, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.hasEffect( Registries.BLEEDING, data->data.entity ) )
				.insertTo( group );

			OnEntityTick.listen( this::tick )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEffect( Registries.BLEEDING, data->data.entity ) )
				.insertTo( group );

			OnDeath.listen( this::spawnParticles )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEffect( Registries.BLEEDING, data->data.target ) )
				.insertTo( group );

			OnEffectApplicable.listen( this::cancelEffect )
				.addCondition( Condition.predicate( data->this.availability.isDisabled() ) )
				.addCondition( Condition.predicate( data->data.effect.equals( Registries.BLEEDING_IMMUNITY.get() ) ) )
				.insertTo( group );

			OnDamaged.listen( this::applyBleeding )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->this.availability.isEnabled() ) )
				.addCondition( Condition.predicate( this::isNotImmune ) )
				.addCondition( OnDamaged.dealtAnyDamage() )
				.insertTo( group );

			OnItemAttributeTooltip.listen( this::addCustomTooltip )
				.addCondition( Condition.predicate( data->this.availability.isEnabled() ) )
				.insertTo( group );
		}

		private void spawnParticles( OnEntityTick.Data data ) {
			int amplifier = MobEffectHelper.getAmplifier( data.entity, Registries.BLEEDING.get() );
			float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

			this.spawnParticles( data.getServerLevel(), data.entity, Random.round( 1.0 + ( 15.0 + amplifier ) * walkDistanceDelta ) );
		}

		private void tick( OnEntityTick.Data data ) {
			LivingEntity entity = data.entity;
			int amplifier = MobEffectHelper.getAmplifier( entity, Registries.BLEEDING.get() );
			int extraDuration = Random.round( 0.3 * ( amplifier + 2 ) * ( 7.26 * EntityHelper.getWalkDistanceDelta( entity ) + 1 ) );
			int duration = this.entityTicks.getOrDefault( entity.getId(), 0 ) + extraDuration;
			if( duration >= BLOOD_TICK_COOLDOWN ) {
				this.dealDamage( entity );
				duration = 0;
			}

			this.entityTicks.put( entity.getId(), duration );
		}

		private void dealDamage( LivingEntity entity ) {
			if( entity.getEffect( Registries.BLEEDING.get() ) instanceof MobEffectInstance effectInstance ) {
				Vec3 motion = entity.getDeltaMovement();
				entity.hurt( new DamageSource( effectInstance.damageSourceEntity ), 1.0f );
				entity.setDeltaMovement( motion ); // sets previous motion to avoid any knockback from bleeding
			} else {
				entity.hurt( Registries.BLEEDING_SOURCE, 1.0f );
			}
			if( entity instanceof ServerPlayer player ) {
				Registries.HELPER.sendMessage( PacketDistributor.PLAYER.with( ()->player ), new BloodMessage( player ) );
			}

			this.entityTicks.put( entity.getId(), 0 );
		}

		private void spawnParticles( OnDeath.Data data ) {
			this.spawnParticles( data.getServerLevel(), data.target, 100 );
		}

		private void spawnParticles( ServerLevel level, Entity entity, int amountOfParticles ) {
			Vec3 position = new Vec3( entity.getX(), entity.getY( 0.5 ), entity.getZ() );
			PARTICLES.spawn( level, position, amountOfParticles );
		}

		private void cancelEffect( OnEffectApplicable.Data data ) {
			data.event.setResult( Event.Result.DENY );
		}

		private void applyBleeding( OnDamaged.Data data ) {
			OnBleedingCheck.Data bleedingData = OnBleedingCheck.dispatch( data.event );
			if( bleedingData.dealtAnyDamage() && bleedingData.isEffectTriggered() && this.effect.apply( data ) ) {
				this.dealDamage( data.target );
			}
		}

		private boolean isNotImmune( OnDamaged.Data data ) {
			return !this.immuneMobs.contains( Utility.getRegistryString( data.target ) );
		}

		private void addCustomTooltip( OnItemAttributeTooltip.Data data ) {
			OnBleedingTooltip.dispatch( data.itemStack, this.effect.getAmplifier() ).addAll( data );
		}
	}

	public static class BloodMessage extends SerializableStructure {
		int entityId;

		public BloodMessage() {
			this.defineInteger( "id", ()->this.entityId, x->this.entityId = x );
		}

		public BloodMessage( Entity entity ) {
			this();

			this.entityId = entity.getId();
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Minecraft minecraft = Minecraft.getInstance();
			if( minecraft.level != null && minecraft.level.getEntity( this.entityId ) == minecraft.player ) {
				BleedingGui.addBloodOnScreen( 3 );
			}
		}
	}
}

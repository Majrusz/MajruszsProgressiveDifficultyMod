package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.majruszsdifficulty.gui.BleedingGui;
import com.mlib.EquipmentSlots;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.*;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.network.NetworkMessage;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
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
	static Supplier< Boolean > IS_ENABLED = ()->true;
	static Supplier< Integer > GET_AMPLIFIER = ()->0;
	static Function< EquipmentSlot, Float > GET_ARMOR_MULTIPLIER = slot->1.0f;

	public static boolean isEnabled() {
		return IS_ENABLED.get();
	}

	public static int getAmplifier() {
		return GET_AMPLIFIER.get();
	}

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
	public static class EntityBleedingDamageSource extends DamageSource {
		@Nullable protected final Entity damageSourceEntity;

		public EntityBleedingDamageSource( @Nullable Entity damageSourceEntity ) {
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
	public static class Bleeding extends GameModifier {
		static final ParticleHandler PARTICLES = new ParticleHandler( Registries.BLOOD, ()->new Vec3( 0.2, 0.5, 0.2 ), ParticleHandler.speed( 0.075f ) );
		static final String ATTRIBUTE_ID = "effect.majruszsdifficulty.bleeding.armor_tooltip";
		static final int BLOOD_TICK_COOLDOWN = Utility.secondsToTicks( 4.0 );
		final StringListConfig immuneMobs = new StringListConfig( "minecraft:skeleton_horse", "minecraft:zombie_horse" );
		final BleedingConfig effect = new BleedingConfig();
		final HashMap< Integer, Integer > entityTicks = new HashMap<>();

		public Bleeding() {
			super( Registries.Modifiers.DEFAULT );

			new OnEntityTick.Context( this::spawnParticles )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 3, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( new Condition.HasEffect<>( Registries.BLEEDING ) )
				.insertTo( this );

			new OnEntityTick.Context( this::tick )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEffect<>( Registries.BLEEDING ) )
				.insertTo( this );

			new OnDeath.Context( this::spawnParticles )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEffect<>( Registries.BLEEDING ) )
				.insertTo( this );

			new OnEffectApplicable.Context( this::cancelEffect )
				.addCondition( data->!BleedingEffect.isEnabled() )
				.addCondition( data->data.effect.equals( Registries.BLEEDING_IMMUNITY.get() ) )
				.insertTo( this );

			new OnDamaged.Context( this::applyBleeding )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new ExcludableBleeding() )
				.addCondition( new ArmorDependentBleedingChance( 0.8, 0.6, 0.7, 0.9 ) )
				.addCondition( this::isNotImmune )
				.addCondition( OnDamaged.DEALT_ANY_DAMAGE )
				.addConfig( this.immuneMobs
					.name( "immune_mobs" )
					.comment( "Specifies which mobs should not be affected by Bleeding (all undead mobs are immune by default)." )
				).addConfig( this.effect )
				.insertTo( this );

			new OnItemAttributeTooltip.Context( this::addChanceTooltip )
				.addCondition( data->data.item instanceof ArmorItem )
				.addCondition( data->BleedingEffect.isEnabled() )
				.insertTo( this );

			GET_AMPLIFIER = this.effect::getAmplifier;

			this.name( "Bleeding" ).comment( "Common config for all Bleeding effects." );
		}

		private void spawnParticles( OnEntityTick.Data data ) {
			int amplifier = MobEffectHelper.getAmplifier( data.entity, Registries.BLEEDING.get() );
			float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

			this.spawnParticles( data.level, data.entity, Random.roundRandomly( 1.0 + ( 15.0 + amplifier ) * walkDistanceDelta ) );
		}

		private void tick( OnEntityTick.Data data ) {
			LivingEntity entity = data.entity;
			int amplifier = MobEffectHelper.getAmplifier( entity, Registries.BLEEDING.get() );
			int extraDuration = Random.roundRandomly( 0.3 * ( amplifier + 2 ) * ( 7.26 * EntityHelper.getWalkDistanceDelta( entity ) + 1 ) );
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
				entity.hurt( new EntityBleedingDamageSource( effectInstance.damageSourceEntity ), 1.0f );
				entity.setDeltaMovement( motion ); // sets previous motion to avoid any knockback from bleeding
			} else {
				entity.hurt( Registries.BLEEDING_SOURCE, 1.0f );
			}
			if( entity instanceof ServerPlayer player ) {
				PacketHandler.CHANNEL.send( PacketDistributor.PLAYER.with( ()->player ), new BloodMessage( player ) );
			}

			this.entityTicks.put( entity.getId(), 0 );
		}

		private void spawnParticles( OnDeath.Data data ) {
			assert data.entity != null;

			this.spawnParticles( data.level, data.entity, 100 );
		}

		private void spawnParticles( ServerLevel level, Entity entity, int amountOfParticles ) {
			Vec3 position = new Vec3( entity.getX(), entity.getY( 0.5 ), entity.getZ() );
			PARTICLES.spawn( level, position, amountOfParticles );
		}

		private void cancelEffect( OnEffectApplicable.Data data ) {
			data.event.setResult( Event.Result.DENY );
		}

		private void applyBleeding( OnDamaged.Data data ) {
			OnBleedingCheck.Data bleedingData = new OnBleedingCheck.Data( data.event );
			OnBleedingCheck.Context.accept( bleedingData );
			if( bleedingData.isEffectTriggered() && this.effect.apply( data ) ) {
				this.dealDamage( data.target );
			}
		}

		private boolean isNotImmune( OnDamaged.Data data ) {
			return !this.immuneMobs.contains( Utility.getRegistryString( data.target ) );
		}

		private void addChanceTooltip( OnItemAttributeTooltip.Data data ) {
			for( EquipmentSlot slot : EquipmentSlots.ARMOR ) {
				if( !data.itemStack.getAttributeModifiers( slot ).containsKey( Attributes.ARMOR ) )
					continue;

				String multiplier = TextHelper.minPrecision( GET_ARMOR_MULTIPLIER.apply( slot ) );
				data.add( slot, Component.translatable( ATTRIBUTE_ID, multiplier ).withStyle( ChatFormatting.BLUE ) );
			}
		}

		private static class ArmorDependentBleedingChance extends Condition.ArmorDependentChance< OnDamaged.Data > {
			ArmorDependentBleedingChance( double headChance, double chestChance, double legsChance, double feetChance ) {
				super( headChance, chestChance, legsChance, feetChance );

				GET_ARMOR_MULTIPLIER = slot->this.multipliers.get( slot ).asFloat();
			}
		}

		private static class ExcludableBleeding extends Condition.Excludable< OnDamaged.Data > {
			ExcludableBleeding() {
				super();

				IS_ENABLED = this.availability::isEnabled;
			}
		}
	}

	public static class BloodMessage extends NetworkMessage {
		final int entityId;

		public BloodMessage( Entity entity ) {
			this.entityId = this.write( entity );
		}

		public BloodMessage( FriendlyByteBuf buffer ) {
			this.entityId = this.readEntity( buffer );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void receiveMessage( NetworkEvent.Context context ) {
			Minecraft minecraft = Minecraft.getInstance();
			if( minecraft.level != null && minecraft.level.getEntity( this.entityId ) == minecraft.player ) {
				BleedingGui.addBloodOnScreen( 3 );
			}
		}
	}
}

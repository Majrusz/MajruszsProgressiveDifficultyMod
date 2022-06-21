package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.entities.EntityHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
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

import static com.majruszsdifficulty.MajruszsDifficulty.GAME_MODIFIERS_GROUP;

/** A bleeding effect similar to the poison effect. */
@Mod.EventBusSubscriber
public class BleedingEffect extends MobEffect {
	protected final ConfigGroup bleedingGroup;
	protected final DoubleConfig damage;
	protected final DurationConfig baseCooldown;
	protected final DoubleConfig armorChanceReduction;
	protected final GameStageIntegerConfig amplifier;
	protected final StringListConfig entitiesBlackList;

	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );

		this.damage = new DoubleConfig( "damage", "Damage dealt by the effect every tick.", false, 1.0, 0.0, 20.0 );
		this.baseCooldown = new DurationConfig( "cooldown", "Cooldown between next tick.", false, 4.0, 0.0, 20.0 );
		this.armorChanceReduction = new DoubleConfig( "armor_reduction", "Chance reduction to apply the effect per each armor piece.", false, 0.2, 0.0, 0.25 );
		this.amplifier = new GameStageIntegerConfig( "amplifier", "Level of the effect.", 0, 1, 2, 0, 10 );
		this.entitiesBlackList = new StringListConfig( "black_list", "List of entities who are immune to the effect. (all entities except human-like mobs and animals are immune by default)", false, "minecraft:skeleton_horse" );
		this.bleedingGroup = GAME_MODIFIERS_GROUP.addGroup( new ConfigGroup( "Bleeding", "Bleeding potion effect.", this.damage, this.baseCooldown, this.armorChanceReduction, this.amplifier, this.entitiesBlackList ) );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {
		float damageAmount = this.damage.get().floatValue();
		BleedingMobEffectInstance effectInstance = Utility.castIfPossible( BleedingMobEffectInstance.class, entity.getEffect( this ) );

		if( effectInstance != null ) {
			Vec3 motion = entity.getDeltaMovement();
			entity.hurt( new EntityBleedingDamageSource( effectInstance.damageSourceEntity ), damageAmount );
			entity.setDeltaMovement( motion ); // sets previous motion to avoid any jumping from bleeding
		} else {
			entity.hurt( Registries.BLEEDING_SOURCE, damageAmount );
		}
	}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = Math.max( 4, this.baseCooldown.getDuration() >> amplifier );

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
		BleedingEffect bleeding = Registries.BLEEDING.get();
		if( event.getEntityLiving().hasEffect( bleeding ) )
			spawnParticles( event.getEntityLiving(), 100 );
	}

	public static boolean isBleedingSource( DamageSource damageSource ) {
		return damageSource.msgId.equals( Registries.BLEEDING_SOURCE.msgId );
	}

	private static void spawnParticles( LivingEntity entity, int amountOfParticles ) {
		ServerLevel level = Utility.castIfPossible( ServerLevel.class, entity.level );
		if( level != null )
			level.sendParticles( Registries.BLOOD.get(), entity.getX(), entity.getY( 0.5 ), entity.getZ(), amountOfParticles, 0.125, 0.5, 0.125, 0.05 );
	}

	public int getAmplifier() {
		return this.amplifier.getCurrentGameStageValue();
	}

	public boolean mayBleed( @Nullable Entity entity ) {
		return ( EntityHelper.isAnimal( entity ) || EntityHelper.isHuman( entity ) ) && !isBlackListed( entity );
	}

	private boolean isBlackListed( @Nullable Entity entity ) {
		return entity != null && this.entitiesBlackList.contains( Utility.getRegistryString( entity.getType() ) );
	}

	public double calculateBleedChanceMultiplier( LivingEntity entity ) {
		double chance = 1.0;
		for( ItemStack armorPiece : entity.getArmorSlots() )
			if( !armorPiece.isEmpty() )
				chance -= this.armorChanceReduction.get();

		return chance;
	}

	/** Bleeding damage source that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class EntityBleedingDamageSource extends DamageSource {
		@Nullable
		protected final Entity damageSourceEntity;

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
	public static class BleedingMobEffectInstance extends MobEffectInstance {
		@Nullable
		protected final Entity damageSourceEntity;

		public BleedingMobEffectInstance( int duration, int amplifier, boolean ambient, boolean showParticles, @Nullable LivingEntity attacker
		) {
			super( Registries.BLEEDING.get(), duration, amplifier, ambient, showParticles );
			this.damageSourceEntity = attacker;
		}
	}
}

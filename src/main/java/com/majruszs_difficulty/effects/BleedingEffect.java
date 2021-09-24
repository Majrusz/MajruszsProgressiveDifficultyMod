package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.CommonHelper;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Bleeding effect similar to poison effect. */
@Mod.EventBusSubscriber
public class BleedingEffect extends MobEffect {
	private static final String BLEEDING_TAG_COUNTER = "BleedingCounter";
	protected final ConfigGroup bleedingGroup;
	protected final DoubleConfig damage;
	protected final DurationConfig baseCooldown;
	protected final DoubleConfig armorChanceReduction;
	protected final GameStateIntegerConfig amplifier;
	protected final StringListConfig entitiesBlackList;

	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );

		String damageComment = "Damage dealt by Bleeding every tick.";
		this.damage = new DoubleConfig( "damage", damageComment, false, 1.0, 0.0, 20.0 );

		String cooldownComment = "Cooldown between taking damage.";
		this.baseCooldown = new DurationConfig( "cooldown", cooldownComment, false, 4.0, 0.0, 20.0 );

		String armorComment = "Chance reduction per each armor piece.";
		this.armorChanceReduction = new DoubleConfig( "armor_reduction", armorComment, false, 0.2, 0.0, 0.25 );

		String amplifierComment = "Bleeding amplifier.";
		this.amplifier = new GameStateIntegerConfig( "amplifier", amplifierComment, 0, 1, 2, 0, 10 );

		String blackComment = "List of entities who are immune to Bleeding effect. (only human-like mobs and animals)";
		this.entitiesBlackList = new StringListConfig( "black_list", blackComment, false, "minecraft:skeleton_horse" );

		this.bleedingGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Bleeding", "Bleeding potion effect." ) );
		this.bleedingGroup.addConfigs( this.damage, this.baseCooldown, this.armorChanceReduction, this.amplifier, this.entitiesBlackList );
	}

	/** Called every time when effect 'isDurationEffectTick'. */
	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {
		float damageAmount = this.damage.get().floatValue();
		BleedingMobEffectInstance effectInstance = CommonHelper.castIfPossible( BleedingMobEffectInstance.class, entity.getEffect( this ) );

		if( effectInstance != null ) {
			Vec3 motion = entity.getDeltaMovement();
			entity.hurt( new EntityBleedingDamageSource( effectInstance.damageSourceEntity ), damageAmount );
			entity.setDeltaMovement( motion ); // sets previous motion to avoid any jumping from bleeding
		} else {
			entity.hurt( Instances.BLEEDING_SOURCE, damageAmount );
		}
	}

	/** When effect starts bleeding will not do anything. */
	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health
	) {}

	/** Calculates whether effect is ready to deal damage. */
	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = Math.max( 4, this.baseCooldown.getDuration() >> amplifier );

		return duration % cooldown == 0;
	}

	/** Removes default milk bucket from curative items. */
	@Override
	public List< ItemStack > getCurativeItems() {
		return new ArrayList<>();
	}

	/** Spawns bleeding particles. */
	@SubscribeEvent
	public static void sendParticles( TickEvent.PlayerTickEvent event ) {
		Player player = event.player;

		if( !( player.level instanceof ServerLevel ) || !( player instanceof ServerPlayer ) )
			return;

		CompoundTag data = player.getPersistentData();
		data.putInt( BLEEDING_TAG_COUNTER, ( data.getInt( BLEEDING_TAG_COUNTER ) + 1 ) % 5 );
		ServerLevel world = ( ServerLevel )player.level;
		if( player.hasEffect( Instances.BLEEDING ) && data.getInt( BLEEDING_TAG_COUNTER ) == 0 )
			world.sendParticles( Instances.BLOOD_PARTICLE, player.getX(), player.getY( 0.5 ), player.getZ(), 1, 0.125, 0.5, 0.125, 0.1 );
	}

	/** Returns bleeding amplifier depending on current game state. */
	public int getAmplifier() {
		return this.amplifier.getCurrentGameStateValue();
	}

	/** Returns whether entity may bleed. */
	public boolean mayBleed( @Nullable Entity entity ) {
		return ( MajruszsHelper.isAnimal( entity ) || MajruszsHelper.isHuman( entity ) ) && !isBlackListed( entity );
	}

	/** Returns whether given entity should not bleed. */
	private boolean isBlackListed( @Nullable Entity entity ) {
		if( entity == null )
			return false;

		EntityType< ? > entityType = entity.getType();
		ResourceLocation entityLocation = entityType.getRegistryName();
		return entityLocation != null && this.entitiesBlackList.contains( entityLocation.toString() );
	}

	/** Returns multiplier that depends on how many armor pieces entity has. */
	public double getChanceMultiplierDependingOnArmor( LivingEntity entity ) {
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
			super( Instances.BLEEDING_SOURCE.msgId );
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

		public BleedingMobEffectInstance( int duration, int amplifier, boolean ambient, boolean showParticles, @Nullable LivingEntity attacker ) {
			super( Instances.BLEEDING, duration, amplifier, ambient, showParticles );
			this.damageSourceEntity = attacker;
		}
	}
}

package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Bleeding effect similar to poison effect. */
@Mod.EventBusSubscriber
public class BleedingEffect extends Effect {
	private static final String BLEEDING_TAG_COUNTER = "BleedingCounter";
	protected final ConfigGroup bleedingGroup;
	protected final DoubleConfig damage;
	protected final DurationConfig baseCooldown;
	protected final DoubleConfig armorChanceReduction;
	protected final GameStateIntegerConfig amplifier;
	protected final StringListConfig entitiesBlackList;

	public BleedingEffect() {
		super( EffectType.HARMFUL, 0xffdd5555 );

		String damageComment = "Damage dealt by bleeding every tick.";
		String cooldownComment = "Cooldown between attacking entity.";
		String armorComment = "Bleeding chance reduction per armor piece.";
		String amplifierComment = "Bleeding amplifier.";
		String blackComment = "List of entities that are immune to Bleeding effect. (only human-like mobs and animals)";
		this.damage = new DoubleConfig( "damage", damageComment, false, 1.0, 0.0, 20.0 );
		this.baseCooldown = new DurationConfig( "cooldown", cooldownComment, false, 4.0, 0.0, 20.0 );
		this.armorChanceReduction = new DoubleConfig( "armor_reduction", armorComment, false, 0.2, 0.0, 0.25 );
		this.amplifier = new GameStateIntegerConfig( "amplifier", amplifierComment, 0, 1, 2, 0, 10 );
		this.entitiesBlackList = new StringListConfig( "black_list", blackComment, false, "minecraft:skeleton_horse" );

		this.bleedingGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Bleeding", "Bleeding potion effect." ) );
		this.bleedingGroup.addConfigs( this.damage, this.baseCooldown, this.armorChanceReduction, this.amplifier, this.entitiesBlackList );
	}

	/** Called every time when effect 'isReady'. */
	@Override
	public void performEffect( LivingEntity entity, int amplifier ) {
		double damageAmount = this.damage.get();

		if( entity.getActivePotionEffect( Instances.BLEEDING ) instanceof BleedingEffectInstance ) {
			BleedingEffectInstance bleedingEffectInstance = ( BleedingEffectInstance )entity.getActivePotionEffect( Instances.BLEEDING );

			Vector3d motion = entity.getMotion();
			entity.attackEntityFrom( bleedingEffectInstance != null ? new EntityBleedingDamageSource(
				bleedingEffectInstance.damageSourceEntity ) : Instances.DamageSources.BLEEDING, ( float )damageAmount );
			entity.setMotion( motion ); // sets previous motion to avoid any jumping from bleeding

		} else {
			entity.attackEntityFrom( Instances.DamageSources.BLEEDING, ( float )damageAmount );
		}
	}

	/** When effect starts bleeding will not do anything. */
	@Override
	public void affectEntity( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	/** Calculating whether effect is ready to deal damage. */
	@Override
	public boolean isReady( int duration, int amplifier ) {
		int cooldown = Math.max( 4, this.baseCooldown.getDuration() >> amplifier );

		return duration % cooldown == 0;
	}

	/** Removes default milk bucket from curative items. */
	@Override
	public List< ItemStack > getCurativeItems() {
		return new ArrayList<>();
	}

	/** Spawning bleeding particles. */
	@SubscribeEvent
	public static void spawnParticles( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;

		if( !( player.world instanceof ServerWorld ) || !( player instanceof ServerPlayerEntity ) )
			return;

		CompoundNBT data = player.getPersistentData();
		data.putInt( BLEEDING_TAG_COUNTER, ( data.getInt( BLEEDING_TAG_COUNTER ) + 1 ) % 5 );
		ServerWorld world = ( ServerWorld )player.world;
		if( player.isPotionActive( Instances.BLEEDING ) && data.getInt( BLEEDING_TAG_COUNTER ) == 0 )
			world.spawnParticle( Instances.BLOOD_PARTICLE, player.getPosX(), player.getPosYHeight( 0.5 ), player.getPosZ(), 1, 0.125, 0.5, 0.125,
				0.1
			);
	}

	/** Returns bleeding amplifier depending on current game state. */
	public int getAmplifier() {
		return this.amplifier.getCurrentGameStateValue();
	}

	/**
	 Returns whether entity may be bleeding.

	 @param entity Entity to test.
	 */
	public boolean mayBleed( @Nullable Entity entity ) {
		return ( MajruszsHelper.isAnimal( entity ) || MajruszsHelper.isHuman( entity ) ) && !isBlackListed( entity );
	}

	/** Returns whether is set that given entity should not bleed. */
	private boolean isBlackListed( @Nullable Entity entity ) {
		if( entity == null )
			return false;

		EntityType< ? > entityType = entity.getType();
		ResourceLocation entityLocation = entityType.getRegistryName();
		return entityLocation != null && this.entitiesBlackList.contains( entityLocation.toString() );
	}

	/**
	 Returns multiplier depending on that how many armor pieces entity is currently wearing.

	 @param entity Entity to test.
	 */
	public double getChanceMultiplierDependingOnArmor( LivingEntity entity ) {
		double chance = 1.0;

		for( ItemStack armorPiece : entity.getArmorInventoryList() )
			if( !armorPiece.isEmpty() )
				chance -= this.armorChanceReduction.get();

		return chance;
	}

	/** Bleeding damage source that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class EntityBleedingDamageSource extends DamageSource {
		@Nullable
		protected final Entity damageSourceEntity;

		public EntityBleedingDamageSource( @Nullable Entity damageSourceEntity ) {
			super( "bleeding" );
			setDamageBypassesArmor();

			this.damageSourceEntity = damageSourceEntity;
		}

		@Nullable
		@Override
		public Entity getTrueSource() {
			return this.damageSourceEntity;
		}
	}

	/** Bleeding effect instance that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class BleedingEffectInstance extends EffectInstance {
		@Nullable
		protected final Entity damageSourceEntity;

		public BleedingEffectInstance( int duration, int amplifier, boolean ambient, boolean showParticles, @Nullable LivingEntity attacker ) {
			super( Instances.BLEEDING, duration, amplifier, ambient, showParticles );
			this.damageSourceEntity = attacker;
		}
	}
}

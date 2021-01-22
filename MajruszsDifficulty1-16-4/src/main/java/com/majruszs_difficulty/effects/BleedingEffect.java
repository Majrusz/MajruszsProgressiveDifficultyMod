package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
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
	protected final ConfigGroup bleedingGroup;
	protected final DoubleConfig damage;
	protected final DurationConfig baseCooldown;
	protected final DoubleConfig armorChanceReduction;
	protected final GameStateIntegerConfig amplifier;

	public BleedingEffect() {
		super( EffectType.HARMFUL, 0xffdd5555 );

		String damage_comment = "Damage dealt by bleeding.";
		String cooldown_comment = "Cooldown between attacking entity.";
		String armor_comment = "Bleeding chance reduction per armor piece.";
		String amplifier_comment = "Bleeding amplifier.";
		String group_comment = "Bleeding effect.";
		this.damage = new DoubleConfig( "damage", damage_comment, false, 1.0, 0.0, 20.0 );
		this.baseCooldown = new DurationConfig( "cooldown", cooldown_comment, false, 5.0, 0.0, 20.0 );
		this.armorChanceReduction = new DoubleConfig( "armor_reduction", armor_comment, false, 0.2, 0.0, 0.25 );
		this.amplifier = new GameStateIntegerConfig( "Amplifier", amplifier_comment, 0, 1, 2, 0, 10 );

		this.bleedingGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Bleeding", group_comment ) );
		this.bleedingGroup.addConfigs( this.damage, this.baseCooldown, this.armorChanceReduction, this.amplifier );
	}

	/** Called every time when effect 'isReady'. */
	@Override
	public void performEffect( LivingEntity entity, int amplifier ) {
		entity.attackEntityFrom( Instances.DamageSources.BLEEDING, ( float )this.damage.get() );
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

	/** Returns bleeding amplifier depending on current game state. */
	public int getAmplifier() {
		return this.amplifier.getCurrentGameStateValue();
	}

	/**
	 Returns whether entity may be bleeding.

	 @param entity Entity to test.
	 */
	public boolean mayBleed( @Nullable Entity entity ) {
		return MajruszsHelper.isAnimal( entity ) || MajruszsHelper.isHuman( entity );
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

	private static int particleCounter = 0;

	/** Spawning bleeding particles. */
	@SubscribeEvent
	public static void spawnParticles( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;

		if( !( player.world instanceof ServerWorld ) || !( player instanceof ServerPlayerEntity ) )
			return;

		particleCounter++;
		if( player.isPotionActive( Instances.BLEEDING ) && particleCounter % 4 == 0 )
			( ( ServerWorld )player.world ).spawnParticle( ( ServerPlayerEntity )player, Instances.BLOOD_PARTICLE, true, player.getPosX(),
				player.getPosYHeight( 0.5 ), player.getPosZ(), 1, 0.125, 0.5, 0.125, 0.1
			);
	}
}

package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;

/** Bleeding effect similar to poison effect. */
public class BleedingEffect extends Effect {
	public static final BleedingEffect instance = new BleedingEffect();

	public BleedingEffect() {
		super( EffectType.HARMFUL, 0xffdd5555 );
	}

	/** Called every time when effect 'isReady'. */
	@Override
	public void performEffect( LivingEntity entity, int amplifier ) {
		entity.attackEntityFrom( RegistryHandler.BLEEDING_SOURCE, 1.0f );
	}

	/** When effect starts bleeding will not do anything. */
	@Override
	public void affectEntity( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	/** Calculating whether effect is ready to deal damage. */
	@Override
	public boolean isReady( int duration, int amplifier ) {
		int cooldown = Math.max( 5, MajruszsHelper.secondsToTicks( 5.0 ) >> amplifier );

		return duration % cooldown == 0;
	}

	/** Returns bleeding amplifier depending on current game state. */
	public static int getAmplifier() {
		switch( GameState.getCurrentMode() ) {
			default:
				return Config.getInteger( Config.Values.BLEEDING_AMPLIFIER_NORMAL );
			case EXPERT:
				return Config.getInteger( Config.Values.BLEEDING_AMPLIFIER_EXPERT );
			case MASTER:
				return Config.getInteger( Config.Values.BLEEDING_AMPLIFIER_MASTER );
		}
	}

	/**
	 Returns whether entity may be bleeding.

	 @param entity Entity to test.
	 */
	public static boolean mayBleed( @Nullable Entity entity ) {
		return MajruszsHelper.isAnimal( entity ) || MajruszsHelper.isHuman( entity );
	}

	/**
	 Returns multiplier depending on that how many armor pieces entity is currently wearing.

	 @param entity Entity to test.
	 */
	public static double getChanceMultiplierDependingOnArmor( LivingEntity entity ) {
		double chance = 1.0;

		for( ItemStack armorPiece : entity.getArmorInventoryList() )
			if( !armorPiece.isEmpty() )
				chance -= 0.2;

		return chance;
	}
}

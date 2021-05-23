package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Makes all Creepers take less damage from other Creepers. */
public class CreeperDamageReductionOnHurt extends WhenDamagedBase {
	private static final String CONFIG_NAME = "CreeperExplosionReduction";
	private static final String CONFIG_COMMENT = "Makes all Creepers take less damage from other Creepers.";
	protected final DoubleConfig damageMultiplier;

	public CreeperDamageReductionOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT );

		String multiplierComment = "Damage multiplier when Creepers attack Creeper.";
		this.damageMultiplier = new DoubleConfig( "damage_multiplier", multiplierComment, false, 0.1, 0.0, 1.0 );
		this.featureGroup.addConfig( this.damageMultiplier );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		event.setAmount( ( float )( event.getAmount() * this.damageMultiplier.get() ) );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof CreeperEntity && target instanceof CreeperEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.blocks.EndShardOre;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Making all nearby endermans to attack player when it hits any enderman. */
public class TriggerAllEndermansOnAttack extends WhenDamagedBase {
	private static final String CONFIG_NAME = "EndermanAttack";
	private static final String CONFIG_COMMENT = "Makes all nearby enderman target player when it hits any of them.";

	public TriggerAllEndermansOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.MASTER );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( attacker != null )
			EndShardOre.targetEndermansOnEntity( attacker, 250.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker != null && target instanceof EndermanEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
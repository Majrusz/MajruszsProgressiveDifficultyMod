package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.blocks.EndShardOre;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/** Making all nearby endermans to attack player when it hits any enderman. */
public class TriggerAllEndermansOnAttack extends WhenDamagedBase {
	private static final String CONFIG_NAME = "EndermanAttack";
	private static final String CONFIG_COMMENT = "Makes all nearby enderman target player when it hits any of them.";

	public TriggerAllEndermansOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0,  GameState.State.MASTER, false );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target ) {
		if( attacker != null )
			EndShardOre.targetEndermansOnEntity( attacker, 500.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker != null && target instanceof EndermanEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.server.ServerWorld;

/** Applying some negative effects on creeper. */
public class ApplyingNegativeEffectOnCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	protected static final int effectDurationInTicks = MajruszsHelper.minutesToTicks( 5.0D );
	protected static final Effect[] effects = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.MINING_FATIGUE, Effects.SATURATION };

	public ApplyingNegativeEffectOnCreeperOnSpawn() {
		super( GameState.State.NORMAL, true );
	}


	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.CREEPER_EFFECTS );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.CREEPER_EFFECTS );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		CreeperEntity creeper = ( CreeperEntity )entity;

		Effect randomEffect = effects[ MajruszsDifficulty.RANDOM.nextInt( effects.length ) ];
		creeper.addPotionEffect( new EffectInstance( randomEffect, effectDurationInTicks, 0 ) );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof CreeperEntity && super.shouldBeExecuted( entity );
	}
}

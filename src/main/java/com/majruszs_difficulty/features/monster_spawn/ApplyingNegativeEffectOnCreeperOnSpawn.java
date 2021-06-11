package com.majruszs_difficulty.features.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.mlib.MajruszLibrary;
import com.mlib.config.DurationConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.server.ServerWorld;

/** Applying some negative effects on creeper. */
public class ApplyingNegativeEffectOnCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	protected static final Effect[] EFFECTS = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.MINING_FATIGUE, Effects.SATURATION };
	private static final String CONFIG_NAME = "CreeperEffects";
	private static final String CONFIG_COMMENT = "Creeper spawns with negative effects applied.";
	protected final DurationConfig duration;

	public ApplyingNegativeEffectOnCreeperOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.375, GameState.State.NORMAL, true );

		String comment = "Duration of effects applied to creeper.";
		this.duration = new DurationConfig( "duration", comment, false, 6.0, 1.0, 60.0 );
		this.featureGroup.addConfig( this.duration );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		CreeperEntity creeper = ( CreeperEntity )entity;

		Effect randomEffect = EFFECTS[ MajruszLibrary.RANDOM.nextInt( EFFECTS.length ) ];
		creeper.addPotionEffect( new EffectInstance( randomEffect, this.duration.getDuration(), 0 ) );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof CreeperEntity && super.shouldBeExecuted( entity );
	}
}

package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.mlib.MajruszLibrary;
import com.mlib.config.DurationConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

/** Applying some negative effects on creeper. */
public class ApplyingNegativeEffectOnCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	protected static final MobEffect[] EFFECTS = new MobEffect[]{ MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN,
		MobEffects.SATURATION
	};
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
	public void onExecute( LivingEntity entity, ServerLevel world ) {
		Creeper creeper = ( Creeper )entity;

		MobEffect randomEffect = EFFECTS[ MajruszLibrary.RANDOM.nextInt( EFFECTS.length ) ];
		creeper.addEffect( new MobEffectInstance( randomEffect, this.duration.getDuration(), 0 ) );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Creeper && super.shouldBeExecuted( entity );
	}
}

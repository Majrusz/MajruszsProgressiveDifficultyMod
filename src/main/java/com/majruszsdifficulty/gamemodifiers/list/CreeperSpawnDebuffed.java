package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnDebuffed extends DifficultyModifier {
	final EffectConfig[] effects = new EffectConfig[]{
		new EffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 60.0 ),
		new EffectConfig( "Slowness", ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( "MiningFatigue", ()->MobEffects.DIG_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( "Saturation", ()->MobEffects.SATURATION, 0, 60.0 )
	};

	public CreeperSpawnDebuffed() {
		super( DifficultyModifier.DEFAULT, "CreeperSpawnDebuffed", "Creeper may spawn with negative effects applied." );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::applyRandomEffect );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 0.375 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.target instanceof Creeper )
			.addConfigs( this.effects );

		this.addContext( onSpawned );
	}

	private void applyRandomEffect( OnSpawnedData data ) {
		Random.nextRandom( this.effects ).apply( data.target );
	}
}

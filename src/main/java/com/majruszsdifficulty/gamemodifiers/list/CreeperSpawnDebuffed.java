package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnDebuffed extends GameModifier {
	final EffectConfig[] effects = new EffectConfig[]{
		new EffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 60.0 ),
		new EffectConfig( "Slowness", ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( "MiningFatigue", ()->MobEffects.DIG_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( "Saturation", ()->MobEffects.SATURATION, 0, 60.0 )
	};

	public CreeperSpawnDebuffed() {
		super( Registries.Modifiers.DEFAULT, "CreeperSpawnDebuffed", "Creeper may spawn with negative effects applied." );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::applyRandomEffect );
		onSpawned.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.375, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
			.addCondition( data->data.target instanceof Creeper )
			.addConfigs( this.effects );

		this.addContext( onSpawned );
	}

	private void applyRandomEffect( OnSpawned.Data data ) {
		Random.nextRandom( this.effects ).apply( data.target );
	}
}

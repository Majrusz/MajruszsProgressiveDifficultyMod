package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperSpawnDebuffed extends GameModifier {
	final EffectConfig[] effects = new EffectConfig[]{
		new EffectConfig( ()->MobEffects.WEAKNESS, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.DIG_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.SATURATION, 0, 60.0 )
	};

	public CreeperSpawnDebuffed() {
		super( Registries.Modifiers.DEFAULT );

		new OnSpawned.ContextSafe( this::applyRandomEffect )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.375, true ) )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new OnSpawned.IsNotLoadedFromDisk<>() )
			.addCondition( data->data.target instanceof Creeper )
			.addConfig( this.effects[ 0 ].name( "Weakness" ) )
			.addConfig( this.effects[ 1 ].name( "Slowness" ) )
			.addConfig( this.effects[ 2 ].name( "MiningFatigue" ) )
			.addConfig( this.effects[ 3 ].name( "Saturation" ) )
			.insertTo( this );

		this.name( "CreeperSpawnDebuffed" ).comment( "Creeper may spawn with negative effects applied." );
	}

	private void applyRandomEffect( OnSpawned.Data data ) {
		Random.nextRandom( this.effects ).apply( data.target );
	}
}

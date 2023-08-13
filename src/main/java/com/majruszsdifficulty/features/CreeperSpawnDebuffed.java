package com.majruszsdifficulty.features;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.mlib.Random;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.EffectConfig;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnSpawned;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperSpawnDebuffed {
	final EffectConfig[] effects = new EffectConfig[]{
		new EffectConfig( ()->MobEffects.WEAKNESS, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.DIG_SLOWDOWN, 0, 60.0 ),
		new EffectConfig( ()->MobEffects.SATURATION, 0, 60.0 )
	};

	public CreeperSpawnDebuffed() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperSpawnDebuffed" )
			.comment( "Creeper may spawn with negative effects applied." );

		OnSpawned.listenSafe( this::applyRandomEffect )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.375, true ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper ) )
			.addConfig( this.effects[ 0 ].name( "Weakness" ) )
			.addConfig( this.effects[ 1 ].name( "Slowness" ) )
			.addConfig( this.effects[ 2 ].name( "MiningFatigue" ) )
			.addConfig( this.effects[ 3 ].name( "Saturation" ) )
			.insertTo( group );
	}

	private void applyRandomEffect( OnSpawned.Data data ) {
		Random.next( this.effects ).apply( data.target );
	}
}

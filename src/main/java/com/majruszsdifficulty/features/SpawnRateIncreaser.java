package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.mlib.modhelper.AutoInstance;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnMobSpawnLimit;
import com.mlib.contexts.OnMobSpawnRate;
import com.mlib.math.Range;
import net.minecraft.world.entity.MobCategory;

@AutoInstance
public class SpawnRateIncreaser {
	final GameStageDoubleConfig spawnRateMultiplier = new GameStageDoubleConfig( 1.0, 1.1, 1.2, new Range<>( 0.0, 10.0 ) );

	public SpawnRateIncreaser() {
		ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpawnRate" )
			.comment( "Increases spawn rate depending on current game stage." )
			.addConfig( this.spawnRateMultiplier );

		OnMobSpawnRate.listen( this::increaseSpawnRate )
			.addCondition( OnMobSpawnRate.is( MobCategory.MONSTER ) );

		OnMobSpawnLimit.listen( this::increaseSpawnLimit )
			.addCondition( OnMobSpawnLimit.is( MobCategory.MONSTER ) );
	}

	private void increaseSpawnRate( OnMobSpawnRate.Data data ) {
		data.value *= this.spawnRateMultiplier.getCurrentGameStageValue();
	}

	private void increaseSpawnLimit( OnMobSpawnLimit.Data data ) {
		data.value *= this.spawnRateMultiplier.getCurrentGameStageValue();
	}
}

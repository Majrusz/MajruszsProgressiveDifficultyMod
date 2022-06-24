package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;

public class JockeySpawn extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new Condition.Chance( 0.125 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Spider && !( data.target instanceof CaveSpider ) ) );
	}

	public JockeySpawn() {
		super( GameModifier.DEFAULT, "JockeySpawn", "Jockey is more likely to spawn.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData && spawnedData.level != null ) {
			Skeleton skeleton = EntityType.SKELETON.create( spawnedData.level );
			if( skeleton == null )
				return;

			skeleton.moveTo( spawnedData.target.getX(), spawnedData.target.getY(), spawnedData.target.getZ(), spawnedData.target.yBodyRot, 0.0f );
			skeleton.finalizeSpawn( spawnedData.level, spawnedData.level.getCurrentDifficultyAt( spawnedData.target.blockPosition() ), MobSpawnType.JOCKEY, null, null );
			skeleton.startRiding( spawnedData.target );
		}
	}
}

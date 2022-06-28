package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
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
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( JockeySpawn::spawnSkeletonOnSpider );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new Condition.Chance( 0.125 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Spider && !( data.target instanceof CaveSpider ) && data.level != null ) );
	}

	public JockeySpawn() {
		super( GameModifier.DEFAULT, "JockeySpawn", "Jockey is more likely to spawn.", ON_SPAWNED );
	}

	private static void spawnSkeletonOnSpider( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		assert data.level != null;
		Skeleton skeleton = EntityType.SKELETON.create( data.level );
		if( skeleton == null )
			return;

		skeleton.moveTo( data.target.getX(), data.target.getY(), data.target.getZ(), data.target.yBodyRot, 0.0f );
		skeleton.finalizeSpawn( data.level, data.level.getCurrentDifficultyAt( data.target.blockPosition() ), MobSpawnType.JOCKEY, null, null );
		skeleton.startRiding( data.target );
	}
}

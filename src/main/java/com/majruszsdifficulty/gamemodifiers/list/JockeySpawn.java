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
		ON_SPAWNED.addCondition( new Condition.Context<>( OnSpawnedContext.Data.class, data->data.target instanceof Spider && !( data.target instanceof CaveSpider ) ) );
	}

	public JockeySpawn() {
		super( GameModifier.DEFAULT, "JockeySpawn", "Jockey is more likely to spawn.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data damagedData && damagedData.level != null ) {
			Skeleton skeleton = EntityType.SKELETON.create( damagedData.level );
			if( skeleton == null )
				return;

			skeleton.moveTo( damagedData.target.getX(), damagedData.target.getY(), damagedData.target.getZ(), damagedData.target.yBodyRot, 0.0f );
			skeleton.finalizeSpawn( damagedData.level, damagedData.level.getCurrentDifficultyAt( damagedData.target.blockPosition() ), MobSpawnType.JOCKEY, null, null );
			skeleton.startRiding( damagedData.target );
		}
	}
}
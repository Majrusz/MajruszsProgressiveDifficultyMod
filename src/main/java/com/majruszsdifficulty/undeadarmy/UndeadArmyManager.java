package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;

import javax.annotation.Nullable;

public class UndeadArmyManager extends GameModifier {
	static final OnSpawnedContext ON_LOADED = new OnSpawnedContext();
	static final OnEntityTickContext ON_TICK = new OnEntityTickContext();
	static {
		ON_LOADED.addCondition( new Condition.ContextOnSpawned( data -> data.loadedFromDisk ) );
		ON_LOADED.addCondition( new Condition.ContextOnSpawned( data -> isUndeadArmy( data.entity ) ) );

		ON_TICK.addCondition( new Condition.ContextOnEntityTick( data -> isUndeadArmy( data.entity ) ) );
	}

	public static boolean isUndeadArmy( @Nullable LivingEntity entity ) {
		return entity != null && !( entity instanceof SkeletonHorse ) && entity.getPersistentData().contains( UndeadArmyKeys.POSITION + "X" );
	}

	public UndeadArmyManager() {
		super( GameModifier.UNDEAD_ARMY, "", "", ON_LOADED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData ) {
			if( spawnedData.context == ON_LOADED ) {
				resetUndeadArmyGoals( spawnedData );
			}
		} else if( data instanceof OnEntityTickContext.Data tickData ) {
			freezeNearbyWater( tickData );
		}
	}

	private static void resetUndeadArmyGoals( OnSpawnedContext.Data spawnedData ) {
		Registries.UNDEAD_ARMY_MANAGER.updateUndeadAIGoals(); // DO NOT UPDATE ALL OF THEM
	}

	private static void freezeNearbyWater( OnEntityTickContext.Data tickData ) {
		assert tickData.entity != null;
		LevelHelper.freezeWater( tickData.entity, 4.0, 30, 60, false );
	}
}

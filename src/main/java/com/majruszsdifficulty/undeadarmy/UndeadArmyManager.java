package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;

import javax.annotation.Nullable;

public class UndeadArmyManager extends GameModifier {
	static final OnSpawnedContext ON_LOADED = new OnSpawnedContext();
	static {
		ON_LOADED.addCondition( new Condition.ContextOnSpawned( data -> data.loadedFromDisk ) );
		ON_LOADED.addCondition( new Condition.ContextOnSpawned( data -> isUndeadArmy( data.entity ) ) );
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
				Registries.UNDEAD_ARMY_MANAGER.updateUndeadAIGoals();
			}
		}
	}
}

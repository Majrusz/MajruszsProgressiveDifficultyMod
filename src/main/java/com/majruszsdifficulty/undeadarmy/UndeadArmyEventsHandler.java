package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.contexts.OnServerTickContext;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.levels.LevelHelper;
import com.mlib.nbt.NBTHelper;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import static com.majruszsdifficulty.undeadarmy.UndeadArmyManager.isUndeadArmy;

public class UndeadArmyEventsHandler extends GameModifier {
	static final OnSpawnedContext ON_LOADED = new OnSpawnedContext( UndeadArmyEventsHandler::resetUndeadArmyGoals );
	static final OnEntityTickContext ON_TICK = new OnEntityTickContext( UndeadArmyEventsHandler::freezeNearbyWater );
	static final OnDeathContext ON_ARMY_PROGRESS = new OnDeathContext( UndeadArmyEventsHandler::updateArmyProgress );
	static final OnDeathContext ON_UNDEAD_KILL = new OnDeathContext( UndeadArmyEventsHandler::updateKilledUndead );
	static final OnServerTickContext ON_SERVER_TICK = new OnServerTickContext( UndeadArmyEventsHandler::tickManager );

	static {
		ON_LOADED.addCondition( new Condition.ContextOnSpawned( data->data.loadedFromDisk && isUndeadArmy( data.entity ) ) );

		ON_TICK.addCondition( new Condition.ContextOnEntityTick( data->isUndeadArmy( data.entity ) ) );

		ON_ARMY_PROGRESS.addCondition( new Condition.ContextOnDeath( data->isUndeadArmy( data.entity ) && Registries.UNDEAD_ARMY_MANAGER != null ) );

		ON_UNDEAD_KILL.addCondition( new Condition.ContextOnDeath( data->data.target.getMobType() == MobType.UNDEAD && Registries.UNDEAD_ARMY_MANAGER != null ) );
		ON_UNDEAD_KILL.addCondition( new Condition.ContextOnDeath( data->data.attacker instanceof Player && !isUndeadArmy( data.target ) ) );

		ON_SERVER_TICK.addCondition( new Condition.ContextOnServerTick( data->data.event.phase == TickEvent.Phase.END && Registries.UNDEAD_ARMY_MANAGER != null ) );
	}

	public UndeadArmyEventsHandler() {
		super( GameModifier.UNDEAD_ARMY, "", "", ON_LOADED, ON_TICK, ON_ARMY_PROGRESS, ON_UNDEAD_KILL, ON_SERVER_TICK );
	}

	private static void resetUndeadArmyGoals( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		// Registries.UNDEAD_ARMY_MANAGER.updateUndeadAIGoals(); // DO NOT UPDATE ALL OF THEM
	}

	private static void freezeNearbyWater( com.mlib.gamemodifiers.GameModifier gameModifier, OnEntityTickContext.Data data ) {
		assert data.entity != null;
		LevelHelper.freezeWater( data.entity, 4.0, 30, 60, false );
	}

	private static void updateArmyProgress( com.mlib.gamemodifiers.GameModifier gameModifier, OnDeathContext.Data data ) {
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( data.target.blockPosition() );
		if( undeadArmy != null ) {
			undeadArmy.increaseUndeadCounter();
		}
	}

	private static void updateKilledUndead( com.mlib.gamemodifiers.GameModifier gameModifier, OnDeathContext.Data data ) {
		assert data.attacker != null;
		NBTHelper.IntegerData killedUndeadData = new NBTHelper.IntegerData( data.attacker, UndeadArmyKeys.KILLED );
		killedUndeadData.set( kills->kills + 1 );
		if( killedUndeadData.get() >= UndeadArmyConfig.getRequiredKills() )
			if( Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( ( Player )data.attacker ) )
				killedUndeadData.set( 0 );
	}

	private static void tickManager( com.mlib.gamemodifiers.GameModifier gameModifier, OnServerTickContext.Data data ) {
		Registries.UNDEAD_ARMY_MANAGER.tick();
	}
}

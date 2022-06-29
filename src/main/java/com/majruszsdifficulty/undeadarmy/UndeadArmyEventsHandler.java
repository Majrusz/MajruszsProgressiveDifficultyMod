package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.contexts.OnServerTickContext;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnDeathData;
import com.mlib.gamemodifiers.data.OnEntityTickData;
import com.mlib.gamemodifiers.data.OnServerTickData;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import com.mlib.levels.LevelHelper;
import com.mlib.nbt.NBTHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import static com.majruszsdifficulty.undeadarmy.UndeadArmyManager.isUndeadArmy;

public class UndeadArmyEventsHandler extends GameModifier {
	public UndeadArmyEventsHandler() {
		super( GameModifier.UNDEAD_ARMY, "", "" );

		OnSpawnedContext onLoaded = new OnSpawnedContext( this::resetUndeadArmyGoals );
		onLoaded.addCondition( new Condition.ContextOnSpawned( data->data.loadedFromDisk && isUndeadArmy( data.entity ) ) );

		OnEntityTickContext onTick = new OnEntityTickContext( this::freezeNearbyWater );
		onTick.addCondition( new Condition.ContextOnEntityTick( data->isUndeadArmy( data.entity ) ) );

		OnDeathContext onArmyProgress = new OnDeathContext( this::updateArmyProgress );
		onArmyProgress.addCondition( new Condition.ContextOnDeath( data->isUndeadArmy( data.entity ) && Registries.UNDEAD_ARMY_MANAGER != null ) );

		OnDeathContext onUndeadKill = new OnDeathContext( this::updateKilledUndead );
		onUndeadKill.addCondition( new Condition.ContextOnDeath( data->data.target.getMobType() == MobType.UNDEAD && Registries.UNDEAD_ARMY_MANAGER != null ) )
			.addCondition( new Condition.ContextOnDeath( data->data.attacker instanceof Player && !isUndeadArmy( data.target ) ) );

		OnServerTickContext onServerTick = new OnServerTickContext( this::tickManager );
		onServerTick.addCondition( new Condition.ContextOnServerTick( data->data.event.phase == TickEvent.Phase.END && Registries.UNDEAD_ARMY_MANAGER != null ) );

		this.addContexts( onLoaded, onTick, onArmyProgress, onUndeadKill, onServerTick );
	}

	private void resetUndeadArmyGoals( OnSpawnedData data ) {
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( data.target.blockPosition() );
		if( undeadArmy != null && data.target instanceof Mob mob ) {
			undeadArmy.addUndeadArmyAI( mob );
		}
	}

	private void freezeNearbyWater( OnEntityTickData data ) {
		assert data.entity != null;
		LevelHelper.freezeWater( data.entity, 4.0, 30, 60, false );
	}

	private void updateArmyProgress( OnDeathData data ) {
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( data.target.blockPosition() );
		if( undeadArmy != null ) {
			undeadArmy.increaseUndeadCounter();
		}
	}

	private void updateKilledUndead( OnDeathData data ) {
		assert data.attacker != null;
		NBTHelper.IntegerData killedUndeadData = new NBTHelper.IntegerData( data.attacker, UndeadArmyKeys.KILLED );
		killedUndeadData.set( kills->kills + 1 );
		if( killedUndeadData.get() >= UndeadArmyConfig.getRequiredKills() )
			if( Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( ( Player )data.attacker ) )
				killedUndeadData.set( 0 );
	}

	private void tickManager( OnServerTickData data ) {
		Registries.UNDEAD_ARMY_MANAGER.tick();
	}
}

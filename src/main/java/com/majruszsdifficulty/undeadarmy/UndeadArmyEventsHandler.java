package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.*;
import com.mlib.levels.LevelHelper;
import com.mlib.nbt.NBTHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import static com.majruszsdifficulty.undeadarmy.UndeadArmyManager.isUndeadArmy;

@AutoInstance
public class UndeadArmyEventsHandler extends GameModifier {
	public UndeadArmyEventsHandler() {
		super( Registries.Modifiers.UNDEAD_ARMY, "UndeadArmyEventsHandler", "" );

		OnSpawned.Context onLoaded = new OnSpawned.Context( this::resetUndeadArmyGoals );
		onLoaded.addCondition( data->data.loadedFromDisk && isUndeadArmy( data.entity ) );

		OnEntityTick.Context onTick = new OnEntityTick.Context( this::freezeNearbyWater );
		onTick.addCondition( data->isUndeadArmy( data.entity ) );

		OnDeath.Context onArmyProgress = new OnDeath.Context( this::updateArmyProgress );
		onArmyProgress.addCondition( data->isUndeadArmy( data.entity ) && Registries.UNDEAD_ARMY_MANAGER != null );

		OnDeath.Context onUndeadKill = new OnDeath.Context( this::updateKilledUndead );
		onUndeadKill.addCondition( data->data.target.getMobType() == MobType.UNDEAD && Registries.UNDEAD_ARMY_MANAGER != null )
			.addCondition( data->data.attacker instanceof Player && !isUndeadArmy( data.target ) )
			.addCondition( data->UndeadArmyConfig.getRequiredKills() > 0 );

		OnServerTick.Context onServerTick = new OnServerTick.Context( this::tickManager );
		onServerTick.addCondition( data->data.event.phase == TickEvent.Phase.END && Registries.UNDEAD_ARMY_MANAGER != null );

		this.addContexts( onLoaded, onTick, onArmyProgress, onUndeadKill, onServerTick );
	}

	private void resetUndeadArmyGoals( OnSpawned.Data data ) {
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( data.target.blockPosition() );
		if( undeadArmy != null && data.target instanceof Mob mob ) {
			undeadArmy.addUndeadArmyAI( mob );
		}
	}

	private void freezeNearbyWater( OnEntityTick.Data data ) {
		assert data.entity != null;
		LevelHelper.freezeWater( data.entity, 4.0, 30, 60, false );
	}

	private void updateArmyProgress( OnDeath.Data data ) {
		UndeadArmy undeadArmy = Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( data.target.blockPosition() );
		if( undeadArmy != null ) {
			undeadArmy.increaseUndeadCounter();
		}
	}

	private void updateKilledUndead( OnDeath.Data data ) {
		assert data.attacker != null;
		NBTHelper.IntegerData killedUndeadData = new NBTHelper.IntegerData( data.attacker, UndeadArmyKeys.KILLED );
		killedUndeadData.set( kills->kills + 1 );
		if( killedUndeadData.get() >= UndeadArmyConfig.getRequiredKills() )
			if( Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( ( Player )data.attacker ) )
				killedUndeadData.set( 0 );
	}

	private void tickManager( OnServerTick.Data data ) {
		Registries.UNDEAD_ARMY_MANAGER.tick();
	}
}

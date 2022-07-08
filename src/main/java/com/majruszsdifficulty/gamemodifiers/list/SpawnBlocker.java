package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageStringListConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnCheckSpawnContext;
import com.mlib.gamemodifiers.data.OnCheckSpawnData;
import com.mlib.levels.LevelHelper;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class SpawnBlocker extends GameModifier {
	final GameStageStringListConfig forbiddenEntities = new GameStageStringListConfig( "", "", new String[]{
		"minecraft:illusioner", "majruszsdifficulty:tank"
	}, new String[]{}, new String[]{} );

	public SpawnBlocker() {
		super( GameModifier.DEFAULT, "SpawnBlocker", "Blocks certain mobs from spawning when given game stage is active." );

		OnCheckSpawnContext onCheckSpawn = new OnCheckSpawnContext( this::blockSpawn );
		onCheckSpawn.addCondition( data->this.isForbidden( data.entity ) )
			.addCondition( data->!( data.entity instanceof Illusioner ) || !isVillageNearby( data.entity ) )
			.addConfig( this.forbiddenEntities );

		this.addContext( onCheckSpawn );
	}

	private void blockSpawn( OnCheckSpawnData data ) {
		data.event.setResult( Event.Result.DENY );
	}

	private boolean isForbidden( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Registry.ENTITY_TYPE.getKey( entity.getType() ).toString() );
	}

	private static boolean isVillageNearby( Entity entity ) {
		if( !( entity.level instanceof ServerLevel level ) || !LevelHelper.isEntityIn( entity, Level.OVERWORLD ) )
			return false;

		return level.findNearestMapStructure( StructureTags.VILLAGE, entity.blockPosition(), 10000, false ) != null;
	}
}

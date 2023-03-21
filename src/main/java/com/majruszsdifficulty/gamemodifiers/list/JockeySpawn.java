package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;

@AutoInstance
public class JockeySpawn {
	public JockeySpawn() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "JockeySpawn" )
			.comment( "Jockey is more likely to spawn." );

		OnSpawned.listenSafe( this::spawnSkeletonOnSpider )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 0.125, false ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( OnSpawned.is( Spider.class ) )
			.insertTo( group );
	}

	private void spawnSkeletonOnSpider( OnSpawned.Data data ) {
		Skeleton skeleton = EntityHelper.spawn( EntityType.SKELETON, data.getServerLevel(), data.target.position() );
		if( skeleton != null ) {
			skeleton.startRiding( data.target );
		}
	}
}

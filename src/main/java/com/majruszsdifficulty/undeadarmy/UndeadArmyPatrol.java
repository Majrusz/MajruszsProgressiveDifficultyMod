package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.undeadarmy.data.ExtraLootInfo;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoInstance
public class UndeadArmyPatrol {
	final MobGroupConfig mobGroups = new MobGroupConfig(
		()->Random.nextRandom( List.of( EntityType.ZOMBIE, EntityType.HUSK, EntityType.SKELETON, EntityType.STRAY ) ),
		new Range<>( 2, 4 ),
		Registries.getLocation( "undead_army/equipment_wave_3" ),
		Registries.getLocation( "undead_army/equipment_wave_2" )
	);

	public UndeadArmyPatrol() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "UndeadArmyPatrol" )
			.comment( "Undead mobs may spawn in groups as the Undead Army Patrol." );

		OnSpawned.listenSafe( this::spawnGroup )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.10, true ) )
			.addCondition( CustomConditions.isNotPartOfGroup( data->data.target ) )
			.addCondition( CustomConditions.isNotPartOfUndeadArmy( data->data.target ) )
			.addCondition( CustomConditions.isNotNearUndeadArmy( data->data.target ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.isLevel( Level.OVERWORLD ) )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( OnSpawned.is( Zombie.class, Skeleton.class, Husk.class, Stray.class ) )
			.addConfigs( this.mobGroups.name( "Undead" ) )
			.insertTo( group );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		PathfinderMob leader = ( PathfinderMob )data.target;
		List< PathfinderMob > mobs = this.mobGroups.spawn( leader );
		mobs.add( leader );
		mobs.forEach( ExtraLootInfo::addExtraLootTag );
	}
}

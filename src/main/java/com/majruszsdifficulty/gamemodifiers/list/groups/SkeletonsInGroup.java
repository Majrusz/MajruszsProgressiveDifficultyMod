package com.majruszsdifficulty.gamemodifiers.list.groups;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;

import java.util.function.Supplier;

@AutoInstance
public class SkeletonsInGroup implements Supplier< MobGroupConfig > {
	final MobGroupConfig mobGroups = new MobGroupConfig(
		()->EntityType.SKELETON,
		new Range<>( 1, 3 ),
		Registries.getLocation( "mob_groups/skeleton_leader" ),
		Registries.getLocation( "mob_groups/skeleton_sidekick" )
	);

	public SkeletonsInGroup() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SkeletonsInGroup" )
			.comment( "Skeletons may spawn in groups." );

		OnSpawned.listenSafe( this::spawnGroup )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 0.1, true ) )
			.addCondition( CustomConditions.isNotPartOfGroup( data->data.target ) )
			.addCondition( CustomConditions.isNotPartOfUndeadArmy( data->data.target ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isServer() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( OnSpawned.is( Skeleton.class ) )
			.addConfig( this.mobGroups.name( "Skeletons" ) )
			.insertTo( group );
	}

	@Override
	public MobGroupConfig get() {
		return this.mobGroups;
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

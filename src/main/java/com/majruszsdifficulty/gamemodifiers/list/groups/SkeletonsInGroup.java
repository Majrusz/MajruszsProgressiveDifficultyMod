package com.majruszsdifficulty.gamemodifiers.list.groups;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;

@AutoInstance
public class SkeletonsInGroup extends GameModifier {
	final MobGroupConfig mobGroups = new MobGroupConfig(
		()->EntityType.SKELETON,
		new Range<>( 1, 3 ),
		Registries.getLocation( "mob_groups/skeleton_leader" ),
		Registries.getLocation( "mob_groups/skeleton_sidekick" )
	);

	public SkeletonsInGroup() {
		super( Registries.Modifiers.DEFAULT );

		OnSpawned.listenSafe( this::spawnGroup )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 0.25, true ) )
			.addCondition( CustomConditions.isNotPartOfGroup( data->data.target ) )
			.addCondition( CustomConditions.isNotPartOfUndeadArmy( data->data.target ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isServer() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( OnSpawned.is( Skeleton.class ) )
			.addConfig( this.mobGroups.name( "Skeletons" ) )
			.insertTo( this );

		this.name( "SkeletonsInGroup" ).comment( "Skeletons may spawn in groups." );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

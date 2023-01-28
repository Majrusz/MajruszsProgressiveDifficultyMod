package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;

@AutoInstance
public class SkeletonsInGroup extends GameModifier {
	final ResourceLocation LEADER_SET_LOCATION = Registries.getLocation( "gameplay/skeleton_group_leader" );
	final ResourceLocation SIDEKICK_SET_LOCATION = Registries.getLocation( "gameplay/skeleton_group_sidekick" );
	final MobGroupConfig mobGroups = new MobGroupConfig( ()->EntityType.SKELETON, new Range<>( 1, 3 ), LEADER_SET_LOCATION, SIDEKICK_SET_LOCATION );

	public SkeletonsInGroup() {
		super( Registries.Modifiers.DEFAULT );

		new OnSpawned.ContextSafe( this::spawnGroup )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.25, true ) )
			.addCondition( new CustomConditions.IsNotPartOfGroup<>() )
			.addCondition( new CustomConditions.IsNotUndeadArmy<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( new OnSpawned.IsNotLoadedFromDisk<>() )
			.addCondition( data->data.target instanceof Skeleton )
			.addConfig( this.mobGroups.name( "Skeletons" ) )
			.insertTo( this );

		this.name( "SkeletonsInGroup" ).comment( "Skeletons may spawn in groups." );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

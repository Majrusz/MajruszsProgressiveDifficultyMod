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
import net.minecraft.world.entity.monster.piglin.Piglin;

import java.util.function.Supplier;

@AutoInstance
public class PiglinsInGroup implements Supplier< MobGroupConfig > {
	final MobGroupConfig mobGroups = new MobGroupConfig(
		()->EntityType.PIGLIN,
		new Range<>( 1, 3 ),
		Registries.getLocation( "mob_groups/piglin_leader" ),
		Registries.getLocation( "mob_groups/piglin_sidekick" )
	);

	public PiglinsInGroup() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "PiglinsInGroup" )
			.comment( "Piglins may spawn in groups." );

		OnSpawned.listenSafe( this::spawnGroup )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 0.25, true ) )
			.addCondition( CustomConditions.isNotPartOfGroup( data->data.target ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( OnSpawned.is( Piglin.class ) )
			.addConfig( this.mobGroups.name( "Piglins" ) )
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

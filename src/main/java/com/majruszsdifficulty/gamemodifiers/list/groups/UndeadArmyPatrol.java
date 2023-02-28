package com.majruszsdifficulty.gamemodifiers.list.groups;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;

import java.util.List;

@AutoInstance
public class UndeadArmyPatrol extends GameModifier {
	final MobGroupConfig mobGroups = new MobGroupConfig(
		()->Random.nextRandom( List.of( EntityType.ZOMBIE, EntityType.HUSK ) ),
		new Range<>( 2, 4 ),
		Registries.getLocation( "undead_army/equipment_wave_3" ),
		Registries.getLocation( "undead_army/equipment_wave_2" )
	);

	public UndeadArmyPatrol() {
		super( Registries.Modifiers.DEFAULT );

		new OnSpawned.ContextSafe( this::spawnGroup )
			.addCondition( new CustomConditions.GameStage<>( GameStage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.0625, true ) )
			.addCondition( new CustomConditions.IsNotPartOfGroup<>() )
			.addCondition( new CustomConditions.IsNotUndeadArmy<>() )
			.addCondition( new CustomConditions.IsNotNearUndeadArmy<>() )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new OnSpawned.IsNotLoadedFromDisk<>() )
			.addCondition( new OnSpawned.Is<>( Zombie.class, Skeleton.class, Husk.class, Stray.class ) )
			.addConfigs( this.mobGroups.name( "Undead" ) )
			.insertTo( this );

		this.name( "UndeadArmyPatrol" ).comment( "Undead may spawn in groups as the Undead Army Patrol." );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

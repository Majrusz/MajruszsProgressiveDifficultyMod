package com.majruszsdifficulty.gamemodifiers.configs;

import com.majruszsdifficulty.goals.FollowGroupLeaderGoal;
import com.majruszsdifficulty.goals.TargetAsLeaderGoal;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IntegerConfig;
import com.mlib.loot.LootHelper;
import com.mlib.math.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MobGroupConfig extends ConfigGroup {
	public static final String SIDEKICK_TAG = "MajruszsDifficultySidekick";
	public static final String LEADER_TAG = "MajruszsDifficultyLeader";
	static final Range< Integer > RANGE = new Range<>( 1, 9 );
	final List< Consumer< PathfinderMob > > onSpawnConsumers = new ArrayList<>();
	final Supplier< EntityType< ? extends PathfinderMob > > mob;
	final IntegerConfig min;
	final IntegerConfig max;
	final ResourceLocation leaderSet;
	final ResourceLocation sidekickSet;

	public MobGroupConfig( Supplier< EntityType< ? extends PathfinderMob > > mob, Range< Integer > range, ResourceLocation leaderSet,
		ResourceLocation sidekickSet
	) {
		this.mob = mob;
		this.min = new IntegerConfig( range.from, RANGE );
		this.max = new IntegerConfig( range.to, RANGE );
		this.leaderSet = leaderSet;
		this.sidekickSet = sidekickSet;

		this.addConfig( this.min.name( "min_count" ).comment( "Minimum amount of mobs to spawn (leader is not considered)." ) );
		this.addConfig( this.max.name( "max_count" ).comment( "Maximum amount of mobs to spawn (leader is not considered)." ) );
	}

	public void onSpawn( Consumer< PathfinderMob > consumer ) {
		this.onSpawnConsumers.add( consumer );
	}

	public List< PathfinderMob > spawn( PathfinderMob leader ) {
		int sidekickAmount = Random.nextInt( getMinCount(), getMaxCount() + 1 );
		Vec3 spawnPosition = leader.position();

		List< PathfinderMob > sidekicks = new ArrayList<>();
		for( int sidekickIdx = 0; sidekickIdx < sidekickAmount; sidekickIdx++ ) {
			PathfinderMob sidekick = this.getMob().create( leader.level );
			if( sidekick == null )
				continue;

			this.addSidekickGoals( sidekick, leader );
			this.markAsSidekick( sidekick );
			this.applyArmorSet( this.sidekickSet, sidekick );
			this.addToLevel( sidekick, spawnPosition );

			sidekicks.add( sidekick );
			this.onSpawnConsumers.forEach( consumer->consumer.accept( sidekick ) );
		}
		this.markAsLeader( leader );
		this.applyArmorSet( this.leaderSet, leader );
		this.onSpawnConsumers.forEach( consumer->consumer.accept( leader ) );

		return sidekicks;
	}

	public EntityType< ? extends PathfinderMob > getMob() {
		return this.mob.get();
	}

	public int getMinCount() {
		return Math.min( this.min.get(), this.max.get() );
	}

	public int getMaxCount() {
		return Math.max( this.min.get(), this.max.get() );
	}

	private void addSidekickGoals( PathfinderMob sidekick, PathfinderMob leader ) {
		sidekick.goalSelector.addGoal( 9, new FollowGroupLeaderGoal( sidekick, leader, 1.0, 6.0f, 5.0f ) );
		sidekick.targetSelector.addGoal( 9, new TargetAsLeaderGoal( sidekick, leader ) );
	}

	private void markAsSidekick( PathfinderMob sidekick ) {
		sidekick.getPersistentData().putBoolean( SIDEKICK_TAG, true );
	}

	private void markAsLeader( PathfinderMob leader ) {
		leader.getPersistentData().putBoolean( LEADER_TAG, true );
	}

	private void applyArmorSet( ResourceLocation location, PathfinderMob mob ) {
		if( location == null )
			return;

		LootHelper.getLootTable( location )
			.getRandomItems( LootHelper.toGiftContext( mob ) )
			.forEach( mob::equipItemIfPossible );

		Arrays.stream( EquipmentSlot.values() )
			.forEach( slot->mob.setDropChance( slot, 0.05f ) );
	}

	private void addToLevel( PathfinderMob sidekick, Vec3 position ) {
		sidekick.setPos( position.x + Random.nextInt( -3, 4 ), position.y + 0.5, position.z + Random.nextInt( -3, 4 ) );
		sidekick.level.addFreshEntity( sidekick );
	}
}

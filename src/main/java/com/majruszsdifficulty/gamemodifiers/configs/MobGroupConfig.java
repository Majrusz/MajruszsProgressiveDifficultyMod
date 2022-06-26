package com.majruszsdifficulty.gamemodifiers.configs;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.goals.FollowGroupLeaderGoal;
import com.majruszsdifficulty.goals.TargetAsLeaderGoal;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IntegerConfig;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MobGroupConfig extends ConfigGroup {
	public static final String SIDEKICK_TAG = "MajruszsDifficultySidekick";
	static final int MIN_COUNT = 1, MAX_COUNT = 9;
	final List< ItemStackConfig > leaderConfigs = new ArrayList<>();
	final List< ItemStackConfig > sidekickConfigs = new ArrayList<>();
	final Supplier< EntityType< ? extends PathfinderMob > > mob;
	final IntegerConfig min;
	final IntegerConfig max;

	public MobGroupConfig( String groupName, Supplier< EntityType< ? extends PathfinderMob > > mob, int min, int max ) {
		super( groupName, "" );
		this.mob = mob;
		this.min = new IntegerConfig( "min_count", "Minimum amount of mobs to spawn (leader is not considered).", false, min, MIN_COUNT, MAX_COUNT );
		this.max = new IntegerConfig( "max_count", "Maximum amount of mobs to spawn (leader is not considered).", false, max, MIN_COUNT, MAX_COUNT );
		this.addConfigs( this.min, this.max );
	}

	public void addLeaderConfigs( ItemStackConfig... configs ) {
		this.leaderConfigs.addAll( List.of( configs ) );
	}

	public void addSidekickConfigs( ItemStackConfig... configs ) {
		this.sidekickConfigs.addAll( List.of( configs ) );
	}

	public List< PathfinderMob > spawn( PathfinderMob leader ) {
		int sidekickAmount = Random.nextInt( getMinCount(), getMaxCount() + 1 );
		Vec3 spawnPosition = leader.position();

		List< PathfinderMob > sidekicks = new ArrayList<>();
		for( int sidekickIdx = 0; sidekickIdx < sidekickAmount; sidekickIdx++ ) {
			PathfinderMob sidekick = this.getMob().create( leader.level );
			if( sidekick == null )
				continue;

			sidekick.setPos( spawnPosition.x + Random.nextInt( -3, 4 ), spawnPosition.y + 0.5, spawnPosition.z + Random.nextInt( -3, 4 ) );
			sidekick.goalSelector.addGoal( 9, new FollowGroupLeaderGoal( sidekick, leader, 1.0, 6.0f, 5.0f ) );
			sidekick.targetSelector.addGoal( 9, new TargetAsLeaderGoal( sidekick, leader ) );
			sidekick.getPersistentData().putBoolean( SIDEKICK_TAG, true );

			leader.level.addFreshEntity( sidekick );
			sidekicks.add( sidekick );
		}
		applyConfigs( leader, sidekicks );

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

	private void applyConfigs( PathfinderMob leader, List< PathfinderMob > sidekicks ) {
		double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( leader );
		for( ItemStackConfig config : this.leaderConfigs ) {
			config.tryToEquip( leader, clampedRegionalDifficulty );
		}
		for( ItemStackConfig config : this.sidekickConfigs ) {
			for( PathfinderMob sidekick : sidekicks ) {
				config.tryToEquip( sidekick, clampedRegionalDifficulty );
			}
		}
	}
}

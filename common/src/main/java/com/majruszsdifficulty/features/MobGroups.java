package com.majruszsdifficulty.features;

import com.majruszlibrary.command.Command;
import com.majruszlibrary.command.CommandData;
import com.majruszlibrary.command.IParameter;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class MobGroups {
	private static Map< String, GroupDef > GROUPS = Map.of(
		"skeletons", new GroupDef(
			GameStageValue.alwaysEnabled(),
			0.1f,
			true,
			List.of(
				new LeaderDef( EntityType.SKELETON, MajruszsDifficulty.HELPER.getLocation( "mob_groups/skeleton_leader" ) )
			),
			Range.of( 1, 3 ),
			List.of(
				new SidekickDef( EntityType.SKELETON, MajruszsDifficulty.HELPER.getLocation( "mob_groups/skeleton_sidekick" ) )
			)
		),
		"zombie_miners", new GroupDef(
			GameStageValue.disabledOn( GameStage.NORMAL_ID ),
			0.25f,
			true,
			List.of(
				new LeaderDef( EntityType.ZOMBIE, MajruszsDifficulty.HELPER.getLocation( "mob_groups/zombie_leader" ) )
			),
			Range.of( 1, 3 ),
			List.of(
				new SidekickDef( EntityType.ZOMBIE, MajruszsDifficulty.HELPER.getLocation( "mob_groups/zombie_sidekick" ) )
			)
		),
		"piglins", new GroupDef(
			GameStageValue.disabledOn( GameStage.NORMAL_ID ),
			0.25f,
			true,
			List.of(
				new LeaderDef( EntityType.PIGLIN, MajruszsDifficulty.HELPER.getLocation( "mob_groups/piglin_leader" ) )
			),
			Range.of( 1, 3 ),
			List.of(
				new SidekickDef( EntityType.PIGLIN, MajruszsDifficulty.HELPER.getLocation( "mob_groups/piglin_sidekick" ) )
			)
		)
	);
	private static final IParameter< String > NAME = Command.string().named( "name" ).suggests( ()->GROUPS.keySet().stream().toList() );

	static {
		OnEntitySpawned.listen( MobGroups::tryToSpawnGroup )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof PathfinderMob );
		// TODO: not undead army

		Command.create()
			.literal( "summongroup" )
			.hasPermission( 4 )
			.parameter( NAME )
			.execute( MobGroups::spawn )
			.register();

		Serializables.getStatic( Config.Features.class )
			.define( "mob_groups", Reader.map( Reader.custom( GroupDef::new ) ), ()->GROUPS, v->GROUPS = v );

		Serializables.get( GroupDef.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), s->s.isEnabled.get(), ( s, v )->s.isEnabled.set( v ) )
			.define( "chance", Reader.number(), s->s.chance, ( s, v )->Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), s->s.isScaledByCRD, ( s, v )->s.isScaledByCRD = v )
			.define( "leader_types", Reader.list( Reader.custom( LeaderDef::new ) ), s->s.leaders, ( s, v )->s.leaders = v )
			.define( "sidekicks_count", Reader.range( Reader.integer() ), s->s.count, ( s, v )->s.count = Range.of( 1, 10 ).clamp( v ) )
			.define( "sidekick_types", Reader.list( Reader.custom( SidekickDef::new ) ), s->s.sidekicks, ( s, v )->s.sidekicks = v );

		Serializables.get( LeaderDef.class )
			.define( "type", Reader.entityType(), s->s.type, ( s, v )->s.type = v )
			.define( "equipment", Reader.location(), s->s.equipment, ( s, v )->s.equipment = v );

		Serializables.get( SidekickDef.class )
			.define( "type", Reader.entityType(), s->s.type, ( s, v )->s.type = v )
			.define( "equipment", Reader.location(), s->s.equipment, ( s, v )->s.equipment = v );
	}

	private static void tryToSpawnGroup( OnEntitySpawned data ) {
		PathfinderMob leader = ( PathfinderMob )data.entity;
		GameStage gameStage = GameStageHelper.determineGameStage( data );
		for( Map.Entry< String, GroupDef > entry : GROUPS.entrySet() ) {
			String id = entry.getKey();
			if( id.equals( "zombie_miners" ) && data.entity.position().y > 50.0f ) {
				continue;
			}

			GroupDef groupDef = entry.getValue();
			if( !groupDef.isEnabled.get( gameStage ) ) {
				continue;
			}

			LeaderDef leaderDef = Random.next( groupDef.leaders.stream().filter( def->def.type.equals( leader.getType() ) ).toList() );
			if( leaderDef == null ) {
				continue;
			}

			double chance = groupDef.chance * ( groupDef.isScaledByCRD ? LevelHelper.getClampedRegionalDifficultyAt( data.getLevel(), leader.blockPosition() ) : 1.0 );
			if( !Random.check( chance ) ) {
				continue;
			}

			MobGroups.spawn( ( PathfinderMob )data.entity, groupDef );
			MobGroups.giveItems( leader, leaderDef.equipment );

			break;
		}
	}

	private static void spawn( PathfinderMob leader, GroupDef groupDef ) {
		Level level = leader.level();
		int count = Random.nextInt( groupDef.count );
		for( int idx = 0; idx < count; ++idx ) {
			SidekickDef sidekickDef = Random.next( groupDef.sidekicks );
			Entity entity = EntityHelper.createSpawner( ()->sidekickDef.type, level )
				.position( MobGroups.getRandomizedPosition( level, leader.position() ) )
				.mobSpawnType( MobSpawnType.EVENT )
				.spawn();
			if( !( entity instanceof PathfinderMob sidekick ) ) {
				continue;
			}

			MobGroups.addSidekickGoals( sidekick, leader );
			MobGroups.giveItems( sidekick, sidekickDef.equipment );
		}
	}

	private static void giveItems( PathfinderMob mob, ResourceLocation id ) {
		LootHelper.getLootTable( id )
			.getRandomItems( LootHelper.toGiftParams( mob ) )
			.forEach( itemStack->ItemHelper.equip( mob, itemStack ) );

		Arrays.stream( EquipmentSlot.values() )
			.forEach( slot->mob.setDropChance( slot, 0.05f ) );
	}

	private static void addSidekickGoals( PathfinderMob sidekick, PathfinderMob leader ) {
		EntityHelper.getGoalSelector( sidekick ).addGoal( 1, new FollowGroupLeaderGoal( sidekick, leader, 1.0, 6.0f, 5.0f ) );
		EntityHelper.getTargetSelector( sidekick ).addGoal( 1, new TargetAsLeaderGoal( sidekick, leader ) );
	}

	private static Vec3 getRandomizedPosition( Level level, Vec3 position ) {
		for( int idx = 0; idx < 3; ++idx ) {
			Vec3 newPosition = AnyPos.from( position ).add( Random.nextInt( -3, 4 ), 0.0, Random.nextInt( -3, 4 ) ).vec3();
			Optional< BlockPos > spawnPoint = LevelHelper.findBlockPosOnGround( level, newPosition.x, Range.of( newPosition.y - 3, newPosition.y + 3 ), newPosition.z );
			if( spawnPoint.isPresent() ) {
				return AnyPos.from( spawnPoint.get() ).add( 0.5, 0.0, 0.5 ).vec3();
			}
		}

		return position;
	}

	private static int spawn( CommandData data ) throws CommandSyntaxException {
		if( !( data.getCaller() instanceof Player player ) ) {
			return -1;
		}

		String name = data.get( NAME );
		GroupDef groupDef = GROUPS.get( name );
		if( groupDef == null ) {
			return -1;
		}

		LeaderDef leaderDef = Random.next( groupDef.leaders );
		Entity entity = EntityHelper.createSpawner( ()->leaderDef.type, player.level() )
			.position( player.position() )
			.mobSpawnType( MobSpawnType.COMMAND )
			.spawn();
		if( !( entity instanceof PathfinderMob leader ) ) {
			return -1;
		}

		MobGroups.spawn( leader, groupDef );
		MobGroups.giveItems( leader, leaderDef.equipment );

		return 0;
	}

	public static class TargetAsLeaderGoal extends TargetGoal {
		private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
		private final PathfinderMob leader;

		public TargetAsLeaderGoal( PathfinderMob sidekick, PathfinderMob leader ) {
			super( sidekick, false );

			this.leader = leader;
			this.setFlags( EnumSet.of( Flag.TARGET ) );
		}

		@Override
		public boolean canUse() {
			return this.leader != null
				&& this.leader.isAlive()
				&& this.canAttack( this.leader.getTarget(), CONDITIONS )
				&& this.leader.getTarget() != this.mob.getTarget();
		}

		@Override
		public void start() {
			this.mob.setTarget( this.leader.getTarget() );
			this.targetMob = this.leader.getTarget();
			this.unseenMemoryTicks = 300;

			super.start();
		}
	}

	public static class FollowGroupLeaderGoal extends Goal {
		private final Mob sidekick;
		private final Mob leader;
		private final double speedModifier;
		private final float maxDistanceFromLeader;
		private final float stopDistance;
		private final PathNavigation navigation;
		private int ticksToRecalculatePath;

		public FollowGroupLeaderGoal( Mob sidekick, Mob leader, double speedModifier, float maxDistanceFromLeader, float stopDistance ) {
			this.sidekick = sidekick;
			this.leader = leader;
			this.navigation = sidekick.getNavigation();
			this.speedModifier = speedModifier;
			this.maxDistanceFromLeader = maxDistanceFromLeader;
			this.stopDistance = stopDistance;
			this.ticksToRecalculatePath = 0;

			this.setFlags( EnumSet.of( Flag.MOVE, Flag.JUMP ) );
		}

		@Override
		public boolean canUse() {
			return this.leader != null
				&& this.leader.isAlive()
				&& this.leader.distanceTo( this.sidekick ) >= this.maxDistanceFromLeader
				&& this.sidekick.getTarget() == null;
		}

		@Override
		public void tick() {
			if( this.leader == null || --this.ticksToRecalculatePath > 0 ) {
				return;
			}

			this.sidekick.getLookControl().setLookAt( this.leader, 10.0F, ( float )this.sidekick.getHeadRotSpeed() );
			this.ticksToRecalculatePath = 20;
			this.navigation.moveTo( this.leader, this.speedModifier );
		}

		@Override
		public boolean canContinueToUse() {
			return this.leader != null
				&& !this.navigation.isDone()
				&& this.sidekick.distanceTo( this.leader ) > this.stopDistance;
		}

		@Override
		public void start() {
			this.ticksToRecalculatePath = 0;
		}
	}

	public static class GroupDef {
		public GameStageValue< Boolean > isEnabled = GameStageValue.alwaysEnabled();
		public float chance = 0.0f;
		public boolean isScaledByCRD = false;
		public List< LeaderDef > leaders = List.of();
		public Range< Integer > count = Range.of( 1, 10 );
		public List< SidekickDef > sidekicks = List.of();

		public GroupDef( GameStageValue< Boolean > isEnabled, float chance, boolean isScaledByCRD, List< LeaderDef > leaders, Range< Integer > count,
			List< SidekickDef > sidekicks
		) {
			this.isEnabled = isEnabled;
			this.chance = chance;
			this.isScaledByCRD = isScaledByCRD;
			this.leaders = leaders;
			this.count = count;
			this.sidekicks = sidekicks;
		}

		public GroupDef() {}
	}

	public static class LeaderDef {
		public EntityType< ? > type;
		public ResourceLocation equipment;

		public LeaderDef( EntityType< ? > type, ResourceLocation equipment ) {
			this.type = type;
			this.equipment = equipment;
		}

		public LeaderDef() {}
	}

	public static class SidekickDef {
		public EntityType< ? > type;
		public ResourceLocation equipment;

		public SidekickDef( EntityType< ? > type, ResourceLocation equipment ) {
			this.type = type;
			this.equipment = equipment;
		}

		public SidekickDef() {}
	}
}

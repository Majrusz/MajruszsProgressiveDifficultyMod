package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyLoaded;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.EnumSet;

public class MobSpawner {
	static {
		OnUndeadArmyTicked.listen( MobSpawner::tryToSpawn )
			.addCondition( data->TimeHelper.haveSecondsPassed( 1.0 ) )
			.addCondition( data->data.undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_ONGOING );

		OnUndeadArmyStateChanged.listen( MobSpawner::generateMobList )
			.addCondition( data->data.undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_PREPARING );

		OnUndeadArmyLoaded.listen( MobSpawner::readdGoals );
	}

	private static void tryToSpawn( OnUndeadArmyTicked data ) {
		data.undeadArmy.mobsLeft.stream()
			.filter( mobDef->mobDef.uuid == null )
			.findFirst()
			.ifPresent( mobInfo->MobSpawner.spawnMob( data.undeadArmy, mobInfo ) );
	}

	private static void generateMobList( OnUndeadArmyStateChanged data ) {
		float sizeMultiplier = 1.0f + ( data.undeadArmy.participants.size() - 1 ) * UndeadArmyConfig.EXTRA_PLAYER_RATIO;
		UndeadArmyConfig.WaveDef waveDef = UndeadArmyConfig.WAVE_DEFS.get( data.undeadArmy.currentWave );
		waveDef.mobDefs.forEach( mobDef->{
			int count = Random.round( mobDef.count * sizeMultiplier );
			for( int idx = 0; idx < count; ++idx ) {
				data.undeadArmy.mobsLeft.add( new UndeadArmy.MobInfo( mobDef, MobSpawner.getRandomSpawnPosition( data.undeadArmy ), false ) );
			}
		} );
		if( waveDef.bossDef != null ) {
			data.undeadArmy.mobsLeft.add( new UndeadArmy.MobInfo( waveDef.bossDef, MobSpawner.getRandomSpawnPosition( data.undeadArmy ), true ) );
		}
		data.undeadArmy.phase.healthTotal = 0;
	}

	private static void readdGoals( OnUndeadArmyLoaded data ) {
		data.undeadArmy.mobsLeft.forEach( mobInfo->{
			if( mobInfo.toEntity( data.getServerLevel() ) instanceof PathfinderMob mob ) {
				MobSpawner.addGoals( mob, data.undeadArmy.position );
			}
		} );
	}

	private static void spawnMob( UndeadArmy undeadArmy, UndeadArmy.MobInfo mobInfo ) {
		Vec3 position = AnyPos.from( mobInfo.position ).add( 0.0, 0.25, 0.0 ).vec3();
		Entity entity = EntityHelper.createSpawner( ()->mobInfo.type, undeadArmy.getLevel() ).position( position ).spawn();
		if( !( entity instanceof PathfinderMob mob ) ) {
			undeadArmy.mobsLeft.remove( mobInfo ); // something went wrong, mob could not spawn, and we do not want to block the Undead Army
			return;
		}

		mobInfo.uuid = mob.getUUID();
		MobSpawner.updateWaveHealth( undeadArmy, mobInfo );
		MobSpawner.tryToLoadEquipment( mob, mobInfo );
		MobSpawner.addGoals( mob, undeadArmy.position );
		MobSpawner.makePersistent( mob );
	}

	private static void updateWaveHealth( UndeadArmy undeadArmy, UndeadArmy.MobInfo mobInfo ) {
		undeadArmy.phase.healthTotal += mobInfo.getMaxHealth( undeadArmy.getLevel() );
	}

	private static Vec3 buildOffset( UndeadArmy undeadArmy ) {
		int spawnRadius = UndeadArmyConfig.AREA_RADIUS - 15;
		UndeadArmy.Direction direction = undeadArmy.direction;
		int x = direction.z != 0 ? 24 : 8;
		int y = 0;
		int z = direction.x != 0 ? 24 : 8;

		return AnyPos.from( direction.x * spawnRadius, 0, direction.z * spawnRadius ).add( Random.nextVector( -x, x, -y, y, -z, z ) ).vec3();
	}

	private static void tryToLoadEquipment( PathfinderMob mob, UndeadArmy.MobInfo mobInfo ) {
		if( mobInfo.equipment == null ) {
			return;
		}

		LootHelper.getLootTable( mobInfo.equipment )
			.getRandomItems( LootHelper.toGiftParams( mob ) )
			.forEach( itemStack->ItemHelper.equip( mob, itemStack ) );

		Arrays.stream( EquipmentSlot.values() )
			.forEach( slot->mob.setDropChance( slot, 0.025f ) );
	}

	private static void addGoals( PathfinderMob mob, BlockPos position ) {
		EntityHelper.getTargetSelector( mob ).addGoal( 0, new UndeadArmyForgiveTeammateGoal( mob ) );
		EntityHelper.getGoalSelector( mob ).addGoal( 4, new UndeadArmyAttackPositionGoal( mob, position ) );
	}

	private static void makePersistent( PathfinderMob mob ) {
		mob.setPersistenceRequired();
	}

	private static BlockPos getRandomSpawnPosition( UndeadArmy undeadArmy ) {
		int tries = 0;
		int x, y, z;
		do {
			Vec3 offset = MobSpawner.buildOffset( undeadArmy );
			x = undeadArmy.position.getX() + ( int )offset.x;
			z = undeadArmy.position.getZ() + ( int )offset.z;
			y = undeadArmy.getLevel().getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
		} while( y != undeadArmy.getLevel().getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && ++tries < 5 );

		return new BlockPos( x, y, z );
	}

	private static class UndeadArmyAttackPositionGoal extends Goal {
		final Mob undead;
		final BlockPos attackPosition;
		final PathNavigation navigation;
		final float speedModifier = 1.25f;
		final float maxDistanceFromPosition = 16.0f;
		int ticksToRecalculatePath = 0;

		public UndeadArmyAttackPositionGoal( Mob mob, BlockPos attackPosition ) {
			this.undead = mob;
			this.navigation = mob.getNavigation();
			this.attackPosition = attackPosition;

			this.setFlags( EnumSet.of( Flag.MOVE ) );
		}

		@Override
		public boolean canUse() {
			return !this.isInRadius()
				&& !this.hasAnyTarget();
		}

		@Override
		public boolean canContinueToUse() {
			return !this.canUse()
				&& !this.navigation.isDone();
		}

		@Override
		public void start() {
			this.ticksToRecalculatePath = 0;
		}

		@Override
		public void tick() {
			if( --this.ticksToRecalculatePath > 0 ) {
				return;
			}

			this.ticksToRecalculatePath = 10;
			this.navigation.moveTo( this.attackPosition.getX(), this.attackPosition.getY(), this.attackPosition.getZ(), this.speedModifier );
		}

		private boolean isInRadius() {
			return this.getDistanceToAttackPosition() < this.maxDistanceFromPosition;
		}

		private boolean hasAnyTarget() {
			return this.undead.getTarget() != null || this.undead.getLastHurtByMob() != null;
		}

		private double getDistanceToAttackPosition() {
			return AnyPos.from( this.undead.position() ).dist2d( AnyPos.from( this.attackPosition ).center() ).doubleValue();
		}
	}

	private static class UndeadArmyForgiveTeammateGoal extends Goal {
		final PathfinderMob mob;

		public UndeadArmyForgiveTeammateGoal( PathfinderMob mob ) {
			this.mob = mob;

			this.setFlags( EnumSet.of( Flag.TARGET ) );
		}

		@Override
		public void start() {
			this.mob.setTarget( null );
			this.mob.setLastHurtByMob( null );
			this.mob.setAggressive( false );
		}

		@Override
		public boolean canUse() {
			return this.mob.getTarget() != null
				&& UndeadArmyHelper.isPartOfUndeadArmy( this.mob )
				&& UndeadArmyHelper.isPartOfUndeadArmy( this.mob.getTarget() );
		}
	}
}

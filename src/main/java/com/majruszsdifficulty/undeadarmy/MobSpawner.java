package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.goals.UndeadArmyAttackPositionGoal;
import com.majruszsdifficulty.goals.UndeadArmyForgiveTeammateGoal;
import com.mlib.Random;
import com.mlib.entities.EntityHelper;
import com.mlib.math.VectorHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

record MobSpawner( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		if( !TimeHelper.hasServerTicksPassed( 20 ) || this.undeadArmy.phase.state != Phase.State.WAVE_ONGOING )
			return;

		MobInfo mobInfo = this.getNextMobToSpawn();
		if( mobInfo != null ) {
			this.spawnMob( mobInfo );
		}
	}

	@Override
	public void onPhaseChanged() {
		if( this.undeadArmy.phase.state == Phase.State.WAVE_PREPARING ) {
			this.generateMobList();
			this.undeadArmy.phase.healthTotal = 0;
		}
	}

	@Override
	public void onGameReload() {
		this.undeadArmy.mobsLeft.forEach( mobInfo->{
			if( mobInfo.toEntity( this.undeadArmy.level ) instanceof PathfinderMob mob ) {
				this.addGoals( mob );
			}
		} );
	}

	@Nullable
	private MobInfo getNextMobToSpawn() {
		return this.undeadArmy.mobsLeft.stream()
			.filter( mobDef->mobDef.uuid == null )
			.findFirst()
			.orElse( null );
	}

	private void spawnMob( MobInfo mobInfo ) {
		Vec3 position = VectorHelper.subtract( VectorHelper.vec3( mobInfo.position ), new Vec3( 0.0, 0.5, 0.0 ) );
		Entity entity = EntityHelper.spawn( mobInfo.type, this.undeadArmy.level, position );
		if( !( entity instanceof PathfinderMob mob ) ) {
			this.undeadArmy.mobsLeft.remove( mobInfo ); // something went wrong, mob could not spawn, and we do not want to block the Undead Army
			return;
		}

		mobInfo.uuid = mob.getUUID();
		this.updateWaveHealth( mobInfo );
		this.addGoals( mob );
		this.makePersistent( mob );
	}

	private void generateMobList() {
		float sizeMultiplier = this.undeadArmy.config.getSizeMultiplier( this.undeadArmy.participants.size() );
		Config.WaveDef waveDef = this.undeadArmy.config.getWave( this.undeadArmy.currentWave + 1 );
		waveDef.mobDefs.forEach( mobDef->{
			int totalCount = Random.roundRandomly( mobDef.count * sizeMultiplier );
			for( int i = 0; i < totalCount; ++i ) {
				this.addToPendingMobs( mobDef, false );
			}
		} );
		if( waveDef.boss != null ) {
			this.addToPendingMobs( waveDef.boss, true );
		}
	}

	private void addToPendingMobs( Config.MobDef def, boolean isBoss ) {
		this.undeadArmy.mobsLeft.add( new MobInfo( def, this.getRandomSpawnPosition(), isBoss ) );
	}

	private void updateWaveHealth( MobInfo mobInfo ) {
		this.undeadArmy.phase.healthTotal += mobInfo.getMaxHealth( this.undeadArmy.level );
	}

	private BlockPos getRandomSpawnPosition() {
		int tries = 0;
		int x, y, z;
		do {
			Vec3i offset = this.buildOffset( 30 );
			x = this.undeadArmy.positionToAttack.getX() + offset.getX();
			z = this.undeadArmy.positionToAttack.getZ() + offset.getZ();
			y = this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
		} while( y != this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && ++tries < 5 );

		return new BlockPos( x, y, z );
	}

	private Vec3i buildOffset( int spawnRadius ) {
		Direction direction = this.undeadArmy.direction;
		int x = direction.z != 0 ? 20 : 10 + direction.x * spawnRadius;
		int y = 0;
		int z = direction.x != 0 ? 20 : 10 + direction.z * spawnRadius;

		return Random.getRandomVector3i( -x, x, -y, y, -z, z );
	}

	private void addGoals( PathfinderMob mob ) {
		mob.goalSelector.addGoal( 4, new UndeadArmyAttackPositionGoal( mob, this.undeadArmy.positionToAttack ) );
		mob.targetSelector.getAvailableGoals().removeIf( wrappedGoal->wrappedGoal.getGoal() instanceof HurtByTargetGoal );
		mob.targetSelector.addGoal( 1, new UndeadArmyForgiveTeammateGoal( mob ) );
	}

	private void makePersistent( PathfinderMob mob ) {
		mob.setPersistenceRequired();
	}
}

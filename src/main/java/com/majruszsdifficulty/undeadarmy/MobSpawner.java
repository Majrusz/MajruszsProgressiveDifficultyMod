package com.majruszsdifficulty.undeadarmy;

import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.entities.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;

public class MobSpawner implements IComponent {
	final UndeadArmy undeadArmy;
	int counter = 0;

	public MobSpawner( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		if( ++this.counter % 10 == 0 && this.undeadArmy.phase == Phase.WAVE_ONGOING && !this.undeadArmy.pendingMobs.isEmpty() ) {
			Config.MobDef mobDef = this.undeadArmy.pendingMobs.remove( 0 );
			Entity entity = EntityHelper.spawn( mobDef.type, this.undeadArmy.level, this.getRandomSpawnPosition().getCenter() );
			if( entity == null )
				return;

			MajruszLibrary.log( "%s %s (%s left) ", mobDef.type, entity.position(), this.undeadArmy.pendingMobs.size() );
		}
	}

	@Override
	public void onPhaseChanged() {
		if( this.undeadArmy.phase == Phase.WAVE_PREPARING ) {
			this.generateMobList();
		}
	}

	private void generateMobList() {
		Config.WaveDef waveDef = this.undeadArmy.config.getWave( this.undeadArmy.currentWave + 1 );
		waveDef.mobDefs.forEach( mobDef->{
			for( int i = 0; i < mobDef.count; ++i ) {
				this.undeadArmy.pendingMobs.add( this.copySingle( mobDef ) );
			}
		} );
		if( waveDef.boss != null ) {
			this.undeadArmy.pendingMobs.add( waveDef.boss );
		}
	}

	private Config.MobDef copySingle( Config.MobDef def ) {
		Config.MobDef copy = new Config.MobDef();
		copy.type = def.type;
		copy.equipment = def.equipment;
		copy.count = 1;

		return copy;
	}

	private BlockPos getRandomSpawnPosition() {
		int tries = 0;
		int x, y, z;
		do {
			Vec3i offset = this.buildOffset( 50 );
			x = this.undeadArmy.positionToAttack.getX() + offset.getX();
			z = this.undeadArmy.positionToAttack.getZ() + offset.getZ();
			y = this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
		} while( y != this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && ++tries < 5 );

		return new BlockPos( x, y, z );
	}

	private Vec3i buildOffset( int spawnRadius ) {
		Direction direction = this.undeadArmy.direction;
		int x = direction.z != 0 ? 50 : 10 + direction.x * spawnRadius;
		int y = 0;
		int z = direction.x != 0 ? 50 : 10 + direction.z * spawnRadius;

		return Random.getRandomVector3i( -x, x, -y, y, -z, z );
	}
}

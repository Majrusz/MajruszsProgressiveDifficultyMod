package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.goals.UndeadArmyAttackPositionGoal;
import com.majruszsdifficulty.goals.UndeadArmyForgiveTeammateGoal;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.*;
import com.mlib.Random;
import com.mlib.entities.EntityHelper;
import com.mlib.items.ItemHelper;
import com.mlib.loot.LootHelper;
import com.mlib.math.AnyPos;
import com.mlib.time.TimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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
	public void onStateChanged() {
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
		Vec3 position = AnyPos.from( mobInfo.position ).add( 0.0, 0.25, 0.0 ).vec3();
		Entity entity = EntityHelper.spawn( mobInfo.type, this.undeadArmy.level, position );
		if( !( entity instanceof PathfinderMob mob ) ) {
			this.undeadArmy.mobsLeft.remove( mobInfo ); // something went wrong, mob could not spawn, and we do not want to block the Undead Army
			return;
		}

		mobInfo.uuid = mob.getUUID();
		this.updateWaveHealth( mobInfo );
		this.tryToLoadEquipment( mob, mobInfo );
		this.addGoals( mob );
		this.makePersistent( mob );
		ExtraLootInfo.addExtraLootTag( mob );
	}

	private void generateMobList() {
		float sizeMultiplier = this.undeadArmy.config.getSizeMultiplier( this.undeadArmy.participants.size() );
		WaveDef waveDef = this.undeadArmy.config.getWave( this.undeadArmy.currentWave + 1 );
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

	private void addToPendingMobs( MobDef def, boolean isBoss ) {
		this.undeadArmy.mobsLeft.add( new MobInfo( def, this.getRandomSpawnPosition(), isBoss ) );
	}

	private void updateWaveHealth( MobInfo mobInfo ) {
		this.undeadArmy.phase.healthTotal += mobInfo.getMaxHealth( this.undeadArmy.level );
	}

	private BlockPos getRandomSpawnPosition() {
		int tries = 0;
		int x, y, z;
		do {
			Vec3 offset = this.buildOffset();
			x = this.undeadArmy.positionToAttack.getX() + ( int )offset.x;
			z = this.undeadArmy.positionToAttack.getZ() + ( int )offset.z;
			y = this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
		} while( y != this.undeadArmy.level.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && ++tries < 5 );

		return new BlockPos( x, y, z );
	}

	private Vec3 buildOffset() {
		int spawnRadius = this.undeadArmy.config.getSpawnRadius();
		Direction direction = this.undeadArmy.direction;
		int x = direction.z != 0 ? 24 : 8;
		int y = 0;
		int z = direction.x != 0 ? 24 : 8;

		return AnyPos.from( direction.x * spawnRadius, 0, direction.z * spawnRadius ).add( Random.getRandomVector( -x, x, -y, y, -z, z ) ).vec3();
	}

	private void tryToLoadEquipment( PathfinderMob mob, MobInfo mobInfo ) {
		if( mobInfo.equipment == null ) {
			return;
		}

		LootHelper.getLootTable( mobInfo.equipment )
			.getRandomItems( LootHelper.toGiftParams( mob, mobInfo.equipment ) )
			.forEach( itemStack->ItemHelper.equip( mob, itemStack ) );

		Arrays.stream( EquipmentSlot.values() )
			.forEach( slot->mob.setDropChance( slot, 0.025f ) );
	}

	private void addGoals( PathfinderMob mob ) {
		mob.targetSelector.addGoal( 11, new UndeadArmyForgiveTeammateGoal( mob ) );
		mob.goalSelector.addGoal( 4, new UndeadArmyAttackPositionGoal( mob, this.undeadArmy.positionToAttack ) );
	}

	private void makePersistent( PathfinderMob mob ) {
		mob.setPersistenceRequired();
	}
}

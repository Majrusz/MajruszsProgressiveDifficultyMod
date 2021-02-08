package com.majruszs_difficulty.events.undead_army;

import com.google.common.collect.Sets;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.goals.UndeadAttackPositionGoal;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.TimeConverter;
import com.mlib.WorldHelper;
import com.mlib.effects.EffectHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.BossInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** Class representing Undead Army which is new raid activated after killing certain undead at night. */
@Mod.EventBusSubscriber
public class UndeadArmy {
	private final static int BETWEEN_RAID_TICKS_MAXIMUM = TimeConverter.secondsToTicks( 10.0 );
	private final static int TICKS_INACTIVE_MAXIMUM = TimeConverter.minutesToTicks( 15.0 );
	private final static int SPAWN_RADIUS = 70;
	private final static TextManager TEXT_MANAGER = new TextManager();
	private final ServerBossInfo bossInfo = new ServerBossInfo( TEXT_MANAGER.title, BossInfo.Color.WHITE, BossInfo.Overlay.NOTCHED_10 );
	private final BlockPos positionToAttack;
	private final Direction direction;
	private ServerWorld world;
	private boolean isActive = true;
	private long ticksActive = 0;
	private int ticksInactive = 0;
	private int ticksWaveActive = 0;
	private int betweenRaidTicks = BETWEEN_RAID_TICKS_MAXIMUM;
	private int currentWave = 0;
	private int undeadToKill = 1;
	private int undeadKilled = 0;
	private Status status = Status.BETWEEN_WAVES;
	private boolean spawnerWasCreated = false;

	public UndeadArmy( ServerWorld world, BlockPos positionToAttack, Direction direction ) {
		this.world = world;
		this.positionToAttack = positionToAttack;
		this.direction = direction;

		this.bossInfo.setPercent( 0.0f );
	}

	public UndeadArmy( ServerWorld world, CompoundNBT nbt ) {
		this.positionToAttack = new BlockPos( nbt.getInt( "PositionX" ), nbt.getInt( "PositionY" ), nbt.getInt( "PositionZ" ) );
		this.direction = Direction.getByName( nbt.getString( "Direction" ) );
		this.world = world;
		this.isActive = nbt.getBoolean( "IsActive" );
		this.ticksActive = nbt.getLong( "TicksActive" );
		this.ticksInactive = nbt.getInt( "TicksInactive" );
		this.ticksWaveActive = nbt.getInt( "TicksWaveActive" );
		this.betweenRaidTicks = nbt.getInt( "BetweenRaidTick" );
		this.currentWave = nbt.getInt( "CurrentWave" );
		this.undeadToKill = nbt.getInt( "UndeadToKill" );
		this.undeadKilled = nbt.getInt( "UndeadKilled" );
		this.status = Status.getByName( nbt.getString( "Status" ) );
		this.spawnerWasCreated = nbt.getBoolean( "SpawnerWasCreated" );

		updateBarText();
	}

	/** Checks whether entity was spawned on Undead Army. */
	public static boolean doesEntityBelongToUndeadArmy( LivingEntity entity ) {
		CompoundNBT data = entity.getPersistentData();

		return data.contains( UndeadArmyTags.UNIT );
	}

	/** Saves information about the raid when saving world. */
	public CompoundNBT write( CompoundNBT nbt ) {
		nbt.putInt( "PositionX", this.positionToAttack.getX() );
		nbt.putInt( "PositionY", this.positionToAttack.getY() );
		nbt.putInt( "PositionZ", this.positionToAttack.getZ() );
		nbt.putString( "Direction", this.direction.toString() );
		nbt.putBoolean( "IsActive", this.isActive );
		nbt.putLong( "TicksActive", this.ticksActive );
		nbt.putInt( "TicksInactive", this.ticksInactive );
		nbt.putInt( "TicksWaveActive", this.ticksWaveActive );
		nbt.putInt( "BetweenRaidTicks", this.betweenRaidTicks );
		nbt.putInt( "CurrentWave", this.currentWave );
		nbt.putInt( "UndeadToKill", this.undeadToKill );
		nbt.putInt( "UndeadKilled", this.undeadKilled );
		nbt.putString( "Status", this.status.toString() );
		nbt.putBoolean( "SpawnerWasCreated", this.spawnerWasCreated );

		return nbt;
	}

	/** Returns position attacked by the Undead Army. */
	public BlockPos getPosition() {
		return this.positionToAttack;
	}

	/** Checks whether Undead Army is still active. */
	public boolean isActive() {
		return this.isActive;
	}

	/** Updates Undead Army world. */
	public void updateWorld( ServerWorld world ) {
		this.world = world;
	}

	/** Updates progression bar text depending on current status. */
	public void updateBarText() {
		switch( this.status ) {
			case ONGOING:
				this.bossInfo.setName( this.currentWave == 0 ? TEXT_MANAGER.title : TEXT_MANAGER.getWaveMessage( this.currentWave ) );
				break;
			case BETWEEN_WAVES:
				this.bossInfo.setName( TEXT_MANAGER.between_waves );
				break;
			case VICTORY:
				this.bossInfo.setName( TEXT_MANAGER.victory );
				break;
			case FAILED:
				this.bossInfo.setName( TEXT_MANAGER.failed );
				break;
			default:
				break;
		}
	}

	/** Function called each tick. */
	public void tick() {
		if( !this.isActive )
			return;

		if( this.ticksActive == 0L )
			TEXT_MANAGER.notifyAboutStart( getNearbyPlayers(), this.direction );

		if( this.ticksActive % 20L == 0L )
			updateUndeadArmyBarVisibility();

		tickCurrentStatus();
		this.ticksActive++;
	}

	/** Function called each tick for handling current status. */
	public void tickCurrentStatus() {
		switch( this.status ) {
			case BETWEEN_WAVES:
				tickBetweenWaves();
				break;
			case ONGOING:
				tickOngoing();
				break;
			case VICTORY:
				tickVictory();
				break;
			case FAILED:
				tickFailed();
				break;
			case STOPPED:
				tickStopped();
				break;
		}
	}

	/** Function called when undead was killed. */
	public void onUndeadKill() {
		this.undeadKilled = Math.min( this.undeadKilled + 1, this.undeadToKill );
	}

	/** Makes all units from Undead Army highlighted. */
	public void highlightUndeadArmy() {
		for( MonsterEntity monster : getNearbyUndeadArmy( SPAWN_RADIUS ) )
			EffectHelper.applyEffectIfPossible( monster, Effects.GLOWING, TimeConverter.secondsToTicks( 15.0 ), 5 );
	}

	/** Counts how many entities are left. */
	public int countUndeadEntitiesLeft() {
		return countNearbyUndeadArmy( SPAWN_RADIUS );
	}

	/** Updates all nearby undead entities AI goals. Required after world restart. */
	public void updateNearbyUndeadGoals() {
		List< MonsterEntity > monsters = getNearbyUndeadArmy( SPAWN_RADIUS );

		for( MonsterEntity monster : monsters )
			updateUndeadGoal( monster );
	}

	/** Ends this undead army. */
	public void finish() {
		this.isActive = false;
		this.bossInfo.removeAllPlayers();
	}

	/** Calculates single frame when waiting on next wave. */
	private void tickBetweenWaves() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.betweenRaidTicks ) / BETWEEN_RAID_TICKS_MAXIMUM, 0.0f, 1.0f ) );

		if( this.betweenRaidTicks == 0 )
			nextWave();
	}

	/** Calculates single frame when wave is active. */
	private void tickOngoing() {
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.undeadKilled ) / this.undeadToKill, 0.0f, 1.0f ) );

		if( countNearbyPlayers() == 0 )
			this.status = Status.STOPPED;

		if( !this.spawnerWasCreated && ( countNearbyUndeadArmy( SPAWN_RADIUS / 7 ) >= this.undeadToKill / 2 ) )
			createSpawner();

		if( this.undeadKilled == this.undeadToKill )
			endWave();

		if( shouldEntitiesBeHighlighted() )
			highlightUndeadArmy();

		this.ticksWaveActive++;
	}

	/** Calculates single frame when Undead Army was defeated. */
	private void tickVictory() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );

		if( this.betweenRaidTicks == 0 )
			finish();
	}

	/** Calculates single frame when Undead Army defeated players. */
	private void tickFailed() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );

		if( this.betweenRaidTicks == 0 )
			finish();
	}

	/** Calculates single frame when there is no player nearby Undead Army. */
	private void tickStopped() {
		this.ticksInactive++;

		if( countNearbyPlayers() > 0 )
			this.status = Status.ONGOING;

		if( this.ticksInactive >= TICKS_INACTIVE_MAXIMUM )
			endWave();
	}

	/** Starts next wave. */
	private void nextWave() {
		++this.currentWave;
		this.status = Status.ONGOING;
		this.ticksWaveActive = 0;
		spawnWaveEnemies();
		updateBarText();
	}

	/** Ends current wave and changes status depending on wave. */
	private void endWave() {
		if( this.ticksInactive >= TICKS_INACTIVE_MAXIMUM ) {
			this.status = Status.FAILED;
			this.betweenRaidTicks = BETWEEN_RAID_TICKS_MAXIMUM * 2;
			this.bossInfo.setPercent( 1.0f );
			this.spawnerWasCreated = false;
			createSpawner();
		} else if( this.currentWave >= getWaves() ) {
			this.status = Status.VICTORY;
			this.betweenRaidTicks = BETWEEN_RAID_TICKS_MAXIMUM * 2;
			rewardPlayers();
			this.bossInfo.setPercent( 1.0f );
		} else {
			this.status = Status.BETWEEN_WAVES;
			this.betweenRaidTicks = BETWEEN_RAID_TICKS_MAXIMUM;
		}

		updateBarText();
	}

	/** Creates monster spawner near attacked position. */
	private void createSpawner() {
		int spawnerRange = 5;
		for( int i = 0; i < 50 && !this.spawnerWasCreated; i++ ) {
			int x = positionToAttack.getX() + MajruszLibrary.RANDOM.nextInt( spawnerRange * 2 + 1 ) - spawnerRange;
			int z = positionToAttack.getZ() + MajruszLibrary.RANDOM.nextInt( spawnerRange * 2 + 1 ) - spawnerRange;
			int y = world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z );
			BlockPos position = new BlockPos( x, y, z );

			if( this.world.isAirBlock( position ) ) {
				this.world.setBlockState( position, Blocks.SPAWNER.getDefaultState() );
				this.spawnerWasCreated = true;

				TileEntity tileEntity = this.world.getTileEntity( position );
				if( !( tileEntity instanceof MobSpawnerTileEntity ) )
					continue;

				( ( MobSpawnerTileEntity )tileEntity ).getSpawnerBaseLogic()
					.setEntityType( getEntityTypeForMonsterSpawner() );
			}
		}

		this.spawnerWasCreated = true;
	}

	/** Spawns monsters depending on current wave. */
	private void spawnWaveEnemies() {
		double playersFactor = 1.0 + ( Math.max( 1, countNearbyPlayers() ) - 1 ) * Instances.UNDEAD_ARMY_CONFIG.scaleWithPlayers.get();
		this.undeadToKill = 0;
		this.undeadKilled = 0;

		for( WaveMember waveMember : WaveMember.values() ) {
			for( int i = 0; i < ( int )( playersFactor * waveMember.waveCounts[ this.currentWave - 1 ] ); i++ ) {
				BlockPos randomPosition = this.direction.getRandomSpawnPosition( this.world, this.positionToAttack, SPAWN_RADIUS );
				MonsterEntity monster = ( MonsterEntity )waveMember.type.spawn( this.world, null, null, randomPosition, SpawnReason.EVENT, true,
					true
				);
				if( monster == null )
					continue;

				monster.enablePersistence();
				updateUndeadGoal( monster );
				tryToEnchantEquipment( monster );
				updateUndeadData( monster );

				this.undeadToKill++;
			}
		}

		int x = this.positionToAttack.getX() + this.direction.x * SPAWN_RADIUS;
		int z = this.positionToAttack.getZ() + this.direction.z * SPAWN_RADIUS;

		for( ServerPlayerEntity player : getNearbyPlayers() )
			player.connection.sendPacket(
				new SPlaySoundEffectPacket( Instances.Sounds.UNDEAD_ARMY_WAVE_STARTED, SoundCategory.NEUTRAL, x, player.getPosY(), z, 64.0f, 1.0f ) );

		this.undeadToKill = Math.max( 1, this.undeadToKill );
	}

	/** Checks whether Undead Army should be highlighted. */
	private boolean shouldEntitiesBeHighlighted() {
		return this.ticksWaveActive >= TimeConverter.minutesToTicks( 1.0 ) && this.ticksWaveActive % 100 == 0 && this.undeadKilled > this.undeadToKill/2;
	}

	/** Tries to enchant weapons and armor for given monster. */
	private void tryToEnchantEquipment( MonsterEntity monster ) {
		double clampedRegionalDifficulty = WorldHelper.getClampedRegionalDifficulty( monster );

		if( monster.hasItemInSlot( EquipmentSlotType.MAINHAND ) && Random.tryChance( getEnchantmentOdds() ) )
			monster.setHeldItem( Hand.MAIN_HAND, ItemHelper.enchantItem( monster.getHeldItemMainhand(), clampedRegionalDifficulty, false ) );

		for( ItemStack armor : monster.getArmorInventoryList() )
			if( Random.tryChance( getEnchantmentOdds() / 2.0 ) ) {
				armor = ItemHelper.enchantItem( armor, clampedRegionalDifficulty, false );
				if( armor.getEquipmentSlot() != null )
					monster.setItemStackToSlot( armor.getEquipmentSlot(), armor );
			}
	}

	/** Rewards all player participating in the raid. */
	private void rewardPlayers() {
		for( PlayerEntity player : this.world.getPlayers( getParticipantsPredicate() ) ) {
			Vector3d position = player.getPositionVec();
			for( int i = 0; i < getExperienceAmount() / 4; i++ )
				this.world.addEntity( new ExperienceOrbEntity( this.world, position.getX(), position.getY() + 1, position.getZ(), 4 ) );

			for( int i = 0; i < getTreasureBagsAmount(); i++ )
				MajruszsHelper.giveItemStackToPlayer( new ItemStack( Instances.TreasureBags.UNDEAD_ARMY ), player, this.world );
		}
	}

	/** Saves information about undead army in monster data. */
	private void updateUndeadData( MonsterEntity monster ) {
		CompoundNBT nbt = monster.getPersistentData();
		nbt.putBoolean( UndeadArmyTags.UNIT, true );
		nbt.putInt( UndeadArmyTags.UNIT_POSITION_X, this.positionToAttack.getX() );
		nbt.putInt( UndeadArmyTags.UNIT_POSITION_Y, this.positionToAttack.getY() );
		nbt.putInt( UndeadArmyTags.UNIT_POSITION_Z, this.positionToAttack.getZ() );
	}

	/** Adds Undead Army AI goal for given monster. */
	private void updateUndeadGoal( MonsterEntity monster ) {
		monster.goalSelector.addGoal( 9, new UndeadAttackPositionGoal( monster, this.positionToAttack, 1.25f, 20.0f, 3.0f ) );
	}

	/** Updates visibility of Undead Army progress bar for nearby players. */
	private void updateUndeadArmyBarVisibility() {
		Set< ServerPlayerEntity > currentPlayers = Sets.newHashSet( this.bossInfo.getPlayers() );
		List< ServerPlayerEntity > validPlayers = getNearbyPlayers();

		for( ServerPlayerEntity player : validPlayers )
			if( !currentPlayers.contains( player ) )
				this.bossInfo.addPlayer( player );

		for( ServerPlayerEntity player : currentPlayers )
			if( !validPlayers.contains( player ) )
				this.bossInfo.removePlayer( player );
	}

	private AxisAlignedBB getAxisAligned( double range ) {
		Vector3i vector = new Vector3i( range, range, range );

		return new AxisAlignedBB( this.positionToAttack.subtract( vector ), this.positionToAttack.add( vector ) );
	}

	/** Predicate for checking whether given monster entity is alive and belongs to the Undead Army. */
	private Predicate< MonsterEntity > getUndeadParticipantsPredicate() {
		return monster->( monster.isAlive() && doesEntityBelongToUndeadArmy( monster ) );
	}

	/** Returns list of nearby Undead Army units. */
	private List< MonsterEntity > getNearbyUndeadArmy( double range ) {
		return this.world.getEntitiesWithinAABB( MonsterEntity.class, getAxisAligned( range ), getUndeadParticipantsPredicate() );
	}

	/** Returns amount of nearby Undead Army units. */
	private int countNearbyUndeadArmy( double range ) {
		return getNearbyUndeadArmy( range ).size();
	}

	/** Predicate for checking whether given player is alive and is participating in the raid. */
	private Predicate< ServerPlayerEntity > getParticipantsPredicate() {
		return player->player.isAlive() && ( RegistryHandler.UNDEAD_ARMY_MANAGER.findUndeadArmy( new BlockPos( player.getPositionVec() ) ) == this );
	}

	/** Returns list of nearby players participating in the raid. */
	private List< ServerPlayerEntity > getNearbyPlayers() {
		return this.world.getPlayers( getParticipantsPredicate() );
	}

	/** Returns amount of nearby players participating in the raid. */
	private int countNearbyPlayers() {
		return getNearbyPlayers().size();
	}

	/** Returns entity type for monster spawner. */
	private EntityType< ? > getEntityTypeForMonsterSpawner() {
		return GameState.getValueDependingOnGameState( EntityType.ZOMBIE, EntityType.SKELETON, EliteSkeletonEntity.type );
	}

	/** Returns amount of treasure bag each player will receive. */
	private int getTreasureBagsAmount() {
		return Instances.UNDEAD_ARMY_CONFIG.treasureBagReward.getCurrentGameStateValue();
	}

	/** Returns amount of experience each player will receive. */
	private int getExperienceAmount() {
		return Instances.UNDEAD_ARMY_CONFIG.experienceReward.getCurrentGameStateValue();
	}

	/** Returns total amount of waves. */
	private int getWaves() {
		return GameState.getValueDependingOnGameState( 3, 4, 5 );
	}

	/** Returns extra chance for entities to have enchanted items. */
	private double getEnchantmentOdds() {
		return Instances.UNDEAD_ARMY_CONFIG.enchantedItems.getCurrentGameStateValue();
	}
}

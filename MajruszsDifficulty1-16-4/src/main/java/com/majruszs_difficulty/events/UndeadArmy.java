package com.majruszs_difficulty.events;

import com.google.common.collect.Sets;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.goals.UndeadAttackPositionGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class UndeadArmy {
	public static class Texts {
		public ITextComponent title = new TranslationTextComponent( "majruszs_difficulty.undead_army.title" );
		public ITextComponent wave = new TranslationTextComponent( "majruszs_difficulty.undead_army.wave" );
		public ITextComponent between_waves = new TranslationTextComponent( "majruszs_difficulty.undead_army.between_waves" );
		public ITextComponent victory = new TranslationTextComponent( "majruszs_difficulty.undead_army.victory" );
		public ITextComponent failed = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
	}

	private final static int betweenRaidTicksMaximum = MajruszsHelper.secondsToTicks( 15.0 );
	private final static int ticksInactiveMaximum = MajruszsHelper.minutesToTicks( 15.0 );
	private final static int spawnRadius = 70;
	private final static int standardDeviation = 10;
	private final static Texts texts = new Texts();
	private final ServerBossInfo bossInfo = new ServerBossInfo( texts.title, BossInfo.Color.WHITE, BossInfo.Overlay.NOTCHED_10 );
	private final BlockPos positionToAttack;
	private final Direction direction;
	private ServerWorld world;
	private boolean isActive = true;
	private long ticksActive = 0;
	private int ticksInactive = 0;
	private int betweenRaidTicks = betweenRaidTicksMaximum;
	private int currentWave = 0;
	private int undeadToKill = 1;
	private int undeadKilled = 0;
	private Status status = Status.BETWEEN_WAVES;

	public UndeadArmy( ServerWorld world, BlockPos positionToAttack, Direction direction ) {
		this.world = world;
		this.positionToAttack = positionToAttack;
		this.direction = direction;

		/*if( this.world.isAirBlock( positionToAttack ) )
			this.world.setBlockState( positionToAttack, Blocks.LAVA.getDefaultState() );*/

		this.bossInfo.setPercent( 0.0f );
	}

	public UndeadArmy( ServerWorld world, CompoundNBT nbt ) {
		this.positionToAttack = new BlockPos( nbt.getInt( "PositionX" ), nbt.getInt( "PositionY" ), nbt.getInt( "PositionZ" ) );
		this.direction = Direction.getByName( nbt.getString( "Direction" ) );
		this.world = world;
		this.isActive = nbt.getBoolean( "IsActive" );
		this.ticksActive = nbt.getLong( "TicksActive" );
		this.ticksInactive = nbt.getInt( "TicksInactive" );
		this.betweenRaidTicks = nbt.getInt( "BetweenRaidTick" );
		this.currentWave = nbt.getInt( "CurrentWave" );
		this.undeadToKill = nbt.getInt( "UndeadToKill" );
		this.undeadKilled = nbt.getInt( "UndeadKilled" );
		this.status = Status.getByName( nbt.getString( "Status" ) );

		updateBarText();
	}

	public CompoundNBT write( CompoundNBT nbt ) {
		nbt.putInt( "PositionX", this.positionToAttack.getX() );
		nbt.putInt( "PositionY", this.positionToAttack.getY() );
		nbt.putInt( "PositionZ", this.positionToAttack.getZ() );
		nbt.putString( "Direction", this.direction.toString() );
		nbt.putBoolean( "IsActive", this.isActive );
		nbt.putLong( "TicksActive", this.ticksActive );
		nbt.putInt( "TicksInactive", this.ticksInactive );
		nbt.putInt( "BetweenRaidTicks", this.betweenRaidTicks );
		nbt.putInt( "CurrentWave", this.currentWave );
		nbt.putInt( "UndeadToKill", this.undeadToKill );
		nbt.putInt( "UndeadKilled", this.undeadKilled );
		nbt.putString( "Status", this.status.toString() );

		return nbt;
	}

	public BlockPos getPosition() {
		return this.positionToAttack;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void updateWorld( ServerWorld world ) {
		this.world = world;
	}

	public void updateBarText() {
		switch( this.status ) {
			case ONGOING:
				this.bossInfo.setName( this.currentWave == 0 ? texts.title : getWaveMessage() );
				break;
			case BETWEEN_WAVES:
				this.bossInfo.setName( texts.between_waves );
				break;
			case VICTORY:
				this.bossInfo.setName( texts.victory );
				break;
			case FAILED:
				this.bossInfo.setName( texts.failed );
				break;
			default:
				break;
		}
	}

	public void tick() {
		if( !this.isActive )
			return;

		this.ticksActive++;

		if( ( this.ticksActive + 19L ) % 20L == 0L )
			updateUndeadArmyBarVisibility();

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

	public void onUndeadKill() {
		this.undeadKilled = Math.min( this.undeadKilled + 1, this.undeadToKill );
	}

	public void updateNearbyUndeadGoals() {
		Vector3i radius = new Vector3i( spawnRadius, spawnRadius, spawnRadius );
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB( this.positionToAttack.subtract( radius ), this.positionToAttack.add( radius ) );

		List< MonsterEntity > monsters = this.world.getEntitiesWithinAABB( MonsterEntity.class, axisAlignedBB, getUndeadParticipantsPredicate() );

		for( MonsterEntity monster : monsters )
			updateUndeadGoal( monster );

		MajruszsDifficulty.LOGGER.info( "Updated " + monsters.size() + "!" );
		// MajruszsDifficulty.LOGGER.info( "UNDEAD ARMY: " + this.world.getEntitiesWithinAABB( Entity.class, axisAlignedBB, entity -> entity.getPersistentData().contains( "UndeadArmyFrostWalker" ) ).size() + "/" + this.world.getEntities().count() );
	}

	private void tickBetweenWaves() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.betweenRaidTicks ) / betweenRaidTicksMaximum, 0.0f, 1.0f ) );

		if( this.betweenRaidTicks == 0 )
			nextWave();
	}

	private void tickOngoing() {
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.undeadKilled ) / this.undeadToKill, 0.0f, 1.0f ) );

		if( getPlayersInRange() == 0 )
			this.status = Status.STOPPED;

		if( this.undeadKilled == this.undeadToKill )
			endWave();
	}

	private void tickVictory() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );

		if( this.betweenRaidTicks == 0 )
			finish();
	}

	private void tickFailed() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );

		if( this.betweenRaidTicks == 0 )
			finish();
	}

	private void tickStopped() {
		this.ticksInactive++;

		if( getPlayersInRange() > 0 )
			this.status = Status.ONGOING;

		if( this.ticksInactive >= ticksInactiveMaximum )
			endWave();
	}

	private void nextWave() {
		++this.currentWave;
		this.status = Status.ONGOING;
		spawnWaveEnemies();
		updateBarText();
	}

	private void endWave() {
		if( this.ticksInactive >= ticksInactiveMaximum ) {
			this.status = Status.FAILED;
			this.betweenRaidTicks = betweenRaidTicksMaximum * 2;
			this.bossInfo.setPercent( 1.0f );
		} else if( this.currentWave >= getWaves() ) {
			this.status = Status.VICTORY;
			this.betweenRaidTicks = betweenRaidTicksMaximum * 2;
			rewardPlayers();
			this.bossInfo.setPercent( 1.0f );
		} else {
			this.status = Status.BETWEEN_WAVES;
			this.betweenRaidTicks = betweenRaidTicksMaximum;
		}

		updateBarText();
	}

	private void spawnWaveEnemies() {
		List< ServerPlayerEntity > players = this.world.getPlayers( getParticipantsPredicate() );
		double playersFactor = 1.0 + ( Math.max( 1, players.size() ) - 1 ) * Config.getDouble( Config.Values.UNDEAD_ARMY_SCALE_WITH_PLAYERS );
		this.undeadToKill = 0;
		this.undeadKilled = 0;

		for( WaveMember waveMember : WaveMember.values() ) {
			for( int i = 0; i < ( int )( playersFactor * waveMember.waveCounts[ this.currentWave - 1 ] ); i++ ) {
				MonsterEntity monster = ( MonsterEntity )waveMember.type.spawn( this.world, null, null, getRandomSpawnPosition(), SpawnReason.EVENT,
					true, true
				);
				if( monster == null )
					continue;

				monster.enablePersistence();
				updateUndeadGoal( monster );
				tryToEnchantEquipment( monster );

				CompoundNBT nbt = monster.getPersistentData();
				nbt.putBoolean( "UndeadArmyFrostWalker", true );
				nbt.putInt( "UndeadArmyPositionX", this.positionToAttack.getX() );
				nbt.putInt( "UndeadArmyPositionY", this.positionToAttack.getY() );
				nbt.putInt( "UndeadArmyPositionZ", this.positionToAttack.getZ() );

				this.undeadToKill++;
			}
		}

		int x = this.positionToAttack.getX() + this.direction.x * spawnRadius;
		int z = this.positionToAttack.getZ() + this.direction.z * spawnRadius;

		for( ServerPlayerEntity player : players )
			player.connection.sendPacket(
				new SPlaySoundEffectPacket( RegistryHandler.UNDEAD_ARMY_WAVE_STARTED.get(), SoundCategory.NEUTRAL, x, player.getPosY(), z, 64.0f,
					1.0f
				) );

		this.undeadToKill = Math.max( 1, this.undeadToKill );
	}

	private void tryToEnchantEquipment( MonsterEntity monster ) {
		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( monster, this.world );

		if( monster.hasItemInSlot( EquipmentSlotType.MAINHAND ) && MajruszsDifficulty.RANDOM.nextDouble() < getEnchantmentOdds() ) {
			ItemStack weapon = monster.getHeldItemMainhand();

			monster.setHeldItem( Hand.MAIN_HAND, MajruszsHelper.enchantItem( weapon, clampedRegionalDifficulty ) );
		}

		for( ItemStack armor : monster.getArmorInventoryList() )
			if( MajruszsDifficulty.RANDOM.nextDouble() < ( getEnchantmentOdds() / 2.0 ) ) {
				armor = MajruszsHelper.enchantItem( armor, clampedRegionalDifficulty );
				if( armor.getEquipmentSlot() != null )
					monster.setItemStackToSlot( armor.getEquipmentSlot(), armor );
			}
	}

	private BlockPos getRandomSpawnPosition() {
		int xFactor = ( this.direction.z != 0 ? 5 : 1 ) * standardDeviation;
		int zFactor = ( this.direction.x != 0 ? 5 : 1 ) * standardDeviation;
		int xx = MajruszsDifficulty.RANDOM.nextInt( xFactor * 2 ) - xFactor;
		int zz = MajruszsDifficulty.RANDOM.nextInt( zFactor * 2 ) - zFactor;
		int x = this.positionToAttack.getX() + this.direction.x * spawnRadius + xx;
		int z = this.positionToAttack.getZ() + this.direction.z * spawnRadius + zz;
		int y = this.world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z );

		return new BlockPos( x, y, z );
	}

	private void rewardPlayers() {
		for( PlayerEntity player : this.world.getPlayers( getParticipantsPredicate() ) ) {
			Vector3d position = player.getPositionVec();
			for( int i = 0; i < getExperienceVictory() / 4; i++ )
				this.world.addEntity( new ExperienceOrbEntity( this.world, position.getX(), position.getY() + 1, position.getZ(), 4 ) );

			ItemStack treasureBag = new ItemStack( RegistryHandler.UNDEAD_TREASURE_BAG.get() );
			if( player.canPickUpItem( treasureBag ) )
				player.inventory.addItemStackToInventory( treasureBag );
			else
				this.world.addEntity( new ItemEntity( this.world, position.getX(), position.getY() + 1, position.getZ(), treasureBag ) );
		}
	}

	private void finish() {
		this.isActive = false;
		this.bossInfo.removeAllPlayers();
	}

	private void updateUndeadGoal( MonsterEntity monster ) {
		monster.goalSelector.addGoal( 9, new UndeadAttackPositionGoal( monster, this.positionToAttack, 1.25f, 25.0f, 5.0f ) );
	}

	private Predicate< MonsterEntity > getUndeadParticipantsPredicate() {
		return monster -> monster.getPersistentData().contains( "UndeadArmyFrostWalker" );
	}

	private Predicate< ServerPlayerEntity > getParticipantsPredicate() {
		return player->player.isAlive() && ( RegistryHandler.undeadArmyManager.findUndeadArmy( new BlockPos( player.getPositionVec() ) ) == this );
	}

	private void updateUndeadArmyBarVisibility() {
		Set< ServerPlayerEntity > currentPlayers = Sets.newHashSet( this.bossInfo.getPlayers() );
		List< ServerPlayerEntity > validPlayers = this.world.getPlayers( getParticipantsPredicate() );

		for( ServerPlayerEntity player : validPlayers )
			if( !currentPlayers.contains( player ) )
				this.bossInfo.addPlayer( player );

		for( ServerPlayerEntity player : currentPlayers )
			if( !validPlayers.contains( player ) )
				this.bossInfo.removePlayer( player );
	}

	private int getPlayersInRange() {
		return this.world.getPlayers( getParticipantsPredicate() )
			.size();
	}

	private int getExperienceVictory() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 40;
			case EXPERT:
				return 80;
			case MASTER:
				return 120;
		}
	}

	private int getWaves() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 3;
			case EXPERT:
				return 4;
			case MASTER:
				return 5;
		}
	}

	private double getEnchantmentOdds() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 0.125;
			case EXPERT:
				return 0.25;
			case MASTER:
				return 0.5;
		}
	}

	private ITextComponent getWaveMessage() {
		IFormattableTextComponent message = new StringTextComponent( "" );
		message.append( texts.title );
		message.appendString( " (" );
		message.append( texts.wave );
		message.appendString( " " + this.currentWave + ")" );

		return message;
	}

	private enum Status {
		BETWEEN_WAVES, ONGOING, VICTORY, FAILED, STOPPED;

		private static Status getByName( String name ) {
			for( Status status : Status.values() )
				if( name.equalsIgnoreCase( status.name() ) )
					return status;

			return ONGOING;
		}
	}

	public enum WaveMember implements IExtensibleEnum {
		ZOMBIE( EntityType.ZOMBIE, new int[]{ 5, 4, 3, 2, 2 } ), HUSK( EntityType.HUSK, new int[]{ 1, 1, 2, 3, 4 } ), GIANT( GiantEntity.type,
			new int[]{ 0, 0, 0, 1, 2 }
		), SKELETON( EntityType.SKELETON, new int[]{ 3, 3, 2, 2, 2 } ), STRAY( EntityType.STRAY, new int[]{ 1, 1, 1, 2, 3 } ), ELITE_SKELETON(
			EliteSkeletonEntity.type, new int[]{ 1, 2, 3, 4, 5 } );

		public final EntityType< ? > type;
		private final int[] waveCounts;

		WaveMember( EntityType< ? > type, int[] waveCounts ) {
			this.type = type;
			this.waveCounts = waveCounts;
		}

		public static WaveMember create( String name, EntityType< ? > type, int[] waveCounts ) {
			throw new IllegalStateException( "Enum not extended" + name + type + Arrays.toString( waveCounts ) ); // weird but required
		}
	}

	public enum Direction {
		WEST( -1, 0 ), EAST( 1, 0 ), NORTH( 0, -1 ), SOUTH( 0, 1 );

		public final int x, z;

		Direction( int x, int z ) {
			this.x = x;
			this.z = z;
		}

		public static Direction getRandom() {
			return Direction.values()[ MajruszsDifficulty.RANDOM.nextInt( Direction.values().length ) ];
		}

		private static Direction getByName( String name ) {
			for( Direction direction : Direction.values() )
				if( name.equalsIgnoreCase( direction.name() ) )
					return direction;

			return WEST;
		}
	}
}

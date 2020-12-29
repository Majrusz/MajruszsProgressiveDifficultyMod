package com.majruszs_difficulty.events;

import com.google.common.collect.Sets;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.goals.UndeadAttackPositionGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.raid.Raid;
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

	private final static int betweenRaidTicksMaximum = MajruszsHelper.secondsToTicks( 6.0 );
	private final static int ticksInactiveMaximum = MajruszsHelper.minutesToTicks( 5.0 );
	private final static int spawnRadius = 50;
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

	private void tickBetweenWaves() {
		this.betweenRaidTicks = Math.max( this.betweenRaidTicks - 1, 0 );
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.betweenRaidTicks ) / betweenRaidTicksMaximum, 0.0f, 1.0f ) );

		if( this.betweenRaidTicks == 0 )
			nextWave();
	}

	private void tickOngoing() {
		this.bossInfo.setPercent( MathHelper.clamp( 1.0f - ( ( float )this.undeadKilled ) / this.undeadToKill, 0.0f, 1.0f ) );

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
		} else if( this.currentWave >= getWaves() ) {
			this.status = Status.VICTORY;
			this.betweenRaidTicks = betweenRaidTicksMaximum * 2;
			rewardPlayers();
		} else {
			this.status = Status.BETWEEN_WAVES;
			this.betweenRaidTicks = betweenRaidTicksMaximum;
		}

		updateBarText();
	}

	private void spawnWaveEnemies() {
		List< ServerPlayerEntity > players = this.world.getPlayers( getParticipantsPredicate() );
		double playersFactor = 1.0 + ( Math.max( 1, players.size() ) - 1 ) * 0.5;
		this.undeadToKill = 0;
		this.undeadKilled = 0;

		for( WaveMember waveMember : WaveMember.values() ) {
			for( int i = 0; i < ( int )( playersFactor * waveMember.waveCounts[ this.currentWave - 1 ] ); i++ ) {
				MonsterEntity monster = ( MonsterEntity )waveMember.type.create( this.world );
				if( monster == null )
					continue;

				BlockPos position = getRandomSpawnPosition();
				monster.setPosition( position.getX(), position.getY(), position.getZ() );
				monster.enablePersistence();
				monster.goalSelector.addGoal( 0, new UndeadAttackPositionGoal( monster, this.positionToAttack, 1.375f, 25.0f, 10.0f ) );
				tryToGiveWeaponTo( monster, waveMember.weaponChance );

				CompoundNBT nbt = monster.getPersistentData();
				nbt.putBoolean( "UndeadArmyFrostWalker", true );
				nbt.putInt( "UndeadArmyPositionX", this.positionToAttack.getX() );
				nbt.putInt( "UndeadArmyPositionY", this.positionToAttack.getY() );
				nbt.putInt( "UndeadArmyPositionZ", this.positionToAttack.getZ() );

				this.world.summonEntity( monster );
				this.undeadToKill++;
			}
		}

		int x = this.positionToAttack.getX() + this.direction.x * spawnRadius;
		int z = this.positionToAttack.getZ() + this.direction.z * spawnRadius;

		for( ServerPlayerEntity player : players )
			player.connection.sendPacket( new SPlaySoundEffectPacket( RegistryHandler.UNDEAD_ARMY_WAVE_STARTED.get(), SoundCategory.NEUTRAL,
				x, player.getPosY(), z, 64.0f, 1.0f ) );

		this.undeadToKill = Math.max( 1, this.undeadToKill );
	}

	private void tryToGiveWeaponTo( MonsterEntity monster, double weaponChance ) {
		ItemStack weapon;

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( monster, this.world );

		if( MajruszsDifficulty.RANDOM.nextDouble() >= weaponChance )
			return;

		boolean isSkeleton = monster instanceof SkeletonEntity || monster instanceof StrayEntity;
		weapon = MajruszsHelper.damageItem( new ItemStack( isSkeleton ? Items.BOW : Items.WOODEN_AXE ) );
		if( MajruszsDifficulty.RANDOM.nextDouble() < getEnchantmentOdds() * clampedRegionalDifficulty )
			weapon = MajruszsHelper.enchantItem( weapon, clampedRegionalDifficulty );

		monster.setHeldItem( Hand.MAIN_HAND, weapon );
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
			for( int i = 0; i < getExperienceVictory(); i++ )
				this.world.addEntity( new ExperienceOrbEntity( this.world, position.getX(), position.getY()+1, position.getZ(), 1 ) );

			ItemStack treasureBag = new ItemStack( RegistryHandler.UNDEAD_TREASURE_BAG.get() );
			if( player.canPickUpItem( treasureBag ) )
				player.inventory.addItemStackToInventory( treasureBag );
			else
				this.world.addEntity( new ItemEntity( this.world, position.getX(), position.getY()+1, position.getZ(), treasureBag ) );
		}
	}

	private void finish() {
		this.isActive = false;
		this.bossInfo.removeAllPlayers();
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

	private int getExperienceVictory() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 30;
			case EXPERT:
				return 40;
			case MASTER:
				return 50;
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
		message.func_230529_a_( texts.title );
		message.func_240702_b_( " (" );
		message.func_230529_a_( texts.wave );
		message.func_240702_b_( " " + this.currentWave + ")" );

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
		ZOMBIE( EntityType.ZOMBIE, new int[]{ 5, 4, 3, 2, 1 }, 0.125 ),
		HUSK( EntityType.HUSK, new int[]{ 1, 1, 2, 3, 4 }, 0.125 ),
		GIANT( GiantEntity.type, new int[]{ 0, 0, 0, 1, 1 }, 0.0 ),
		SKELETON( EntityType.SKELETON, new int[]{ 3, 3, 2, 2, 2 }, 1.0 ),
		STRAY( EntityType.STRAY, new int[]{ 1, 1, 2, 3, 4 }, 1.0 ),
		ELITE_SKELETON( EliteSkeletonEntity.type, new int[]{ 1, 2, 3, 4, 5 }, 1.0 );

		public final EntityType< ? > type;
		private final int[] waveCounts;
		private final double weaponChance;

		WaveMember( EntityType< ? > type, int[] waveCounts, double weaponChance ) {
			this.type = type;
			this.waveCounts = waveCounts;
			this.weaponChance = weaponChance;
		}

		public static WaveMember create( String name, EntityType< ? > type, int[] waveCounts, double weaponChance ) {
			throw new IllegalStateException( "Enum not extended" + name + type + Arrays.toString( waveCounts ) + weaponChance ); // weird but required
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

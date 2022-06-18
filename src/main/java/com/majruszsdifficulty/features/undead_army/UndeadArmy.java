package com.majruszsdifficulty.features.undead_army;

import com.google.common.collect.Sets;
import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.RegistryHandler;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.goals.ForgiveUndeadArmyTargetGoal;
import com.majruszsdifficulty.goals.UndeadAttackPositionGoal;
import com.mlib.Utility;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.effects.EffectHelper;
import com.mlib.items.ItemHelper;
import com.mlib.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** Class representing Undead Army raid. (new raid that starts after killing certain amount of undead) */
@Mod.EventBusSubscriber
public class UndeadArmy {
	public final static int ARMOR_COLOR = 0x92687b;
	private final static int SAFE_SPAWN_RADIUS = 90;
	private final static int SPAWN_RADIUS = 70;
	private final ServerBossEvent bossInfo = new ServerBossEvent( UndeadArmyText.TITLE, BossEvent.BossBarColor.WHITE,
		BossEvent.BossBarOverlay.NOTCHED_10
	);
	private final BlockPos positionToAttack;
	private final Direction direction;
	private Status status;
	private boolean isActive, spawnerWasCreated;
	private int ticksActive, ticksInactive, ticksInactiveMaximum, ticksWaveActive, ticksBetweenWaves, ticksBetweenWavesMaximum, currentWave, undeadToKill, undeadKilled;
	private ServerLevel level;

	public UndeadArmy( ServerLevel level, BlockPos positionToAttack, Direction direction ) {
		this.positionToAttack = positionToAttack;
		this.direction = direction;
		this.status = Status.BETWEEN_WAVES;
		this.isActive = true;
		this.spawnerWasCreated = false;
		this.ticksActive = 0;
		this.ticksInactive = 0;
		this.ticksWaveActive = 0;
		this.ticksBetweenWaves = 0;
		this.currentWave = 0;
		this.undeadToKill = 1;
		this.undeadKilled = 0;
		this.level = level;

		setConfigurationValues();
		this.bossInfo.setProgress( 0.0f );
	}

	public UndeadArmy( ServerLevel level, CompoundTag nbt ) {
		this.positionToAttack = NBTHelper.loadBlockPos( nbt, UndeadArmyKeys.POSITION );
		this.direction = Direction.getByName( nbt.getString( UndeadArmyKeys.DIRECTION ) );
		this.status = Status.getByName( nbt.getString( UndeadArmyKeys.STATUS ) );
		this.isActive = nbt.getBoolean( UndeadArmyKeys.ACTIVE );
		this.spawnerWasCreated = nbt.getBoolean( UndeadArmyKeys.SPAWNER );
		this.ticksActive = nbt.getInt( UndeadArmyKeys.TICKS_ACTIVE );
		this.ticksInactive = nbt.getInt( UndeadArmyKeys.TICKS_INACTIVE );
		this.ticksWaveActive = nbt.getInt( UndeadArmyKeys.TICKS_WAVE );
		this.ticksBetweenWaves = nbt.getInt( UndeadArmyKeys.TICKS_BETWEEN );
		this.currentWave = nbt.getInt( UndeadArmyKeys.WAVE );
		this.undeadToKill = nbt.getInt( UndeadArmyKeys.TO_KILL );
		this.undeadKilled = nbt.getInt( UndeadArmyKeys.KILLED );
		this.level = level;

		setConfigurationValues();
		updateBarText();
	}

	/** Checks whether entity belongs to the Undead Army. */
	public static boolean doesEntityBelongToUndeadArmy( LivingEntity entity ) {
		return entity.getPersistentData().contains( UndeadArmyKeys.POSITION + "X" ) && !( entity instanceof SkeletonHorse );
	}

	/** Sets the value of variables that depends on config values. */
	private void setConfigurationValues() {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		this.ticksBetweenWaves = this.ticksBetweenWavesMaximum = config.getAmountOfTicksBetweenWaves();
		this.ticksInactiveMaximum = config.getAmountOfInactivityTicks();
	}

	/** Saves information about the raid when saving world. */
	public CompoundTag write( CompoundTag nbt ) {
		NBTHelper.saveBlockPos( nbt, UndeadArmyKeys.POSITION, this.positionToAttack );
		nbt.putString( UndeadArmyKeys.DIRECTION, String.valueOf( this.direction ) );
		nbt.putString( UndeadArmyKeys.STATUS, String.valueOf( this.status ) );
		nbt.putBoolean( UndeadArmyKeys.ACTIVE, this.isActive );
		nbt.putBoolean( UndeadArmyKeys.SPAWNER, this.spawnerWasCreated );
		nbt.putInt( UndeadArmyKeys.TICKS_ACTIVE, this.ticksActive );
		nbt.putInt( UndeadArmyKeys.TICKS_INACTIVE, this.ticksInactive );
		nbt.putInt( UndeadArmyKeys.TICKS_WAVE, this.ticksWaveActive );
		nbt.putInt( UndeadArmyKeys.TICKS_BETWEEN, this.ticksBetweenWaves );
		nbt.putInt( UndeadArmyKeys.WAVE, this.currentWave );
		nbt.putInt( UndeadArmyKeys.TO_KILL, this.undeadToKill );
		nbt.putInt( UndeadArmyKeys.KILLED, this.undeadKilled );

		return nbt;
	}

	/** Returns the position attacked by the Undead Army. */
	public BlockPos getAttackPosition() {
		return this.positionToAttack;
	}

	/** Checks whether the Undead Army is still active. */
	public boolean isActive() {
		return this.isActive;
	}

	/** Updates the Undead Army world. */
	public void updateWorld( ServerLevel world ) {
		this.level = world;
	}

	/** Updates progression bar text depending on current status. */
	public void updateBarText() {
		switch( this.status ) {
			case ONGOING:
				this.bossInfo.setName( this.currentWave == 0 ? UndeadArmyText.TITLE : UndeadArmyText.getWaveMessage( this.currentWave ) );
				break;
			case BETWEEN_WAVES:
				this.bossInfo.setName( UndeadArmyText.BETWEEN_WAVES );
				break;
			case VICTORY:
				this.bossInfo.setName( UndeadArmyText.VICTORY );
				break;
			case FAILED:
				this.bossInfo.setName( UndeadArmyText.FAILED );
				break;
			default:
				break;
		}
	}

	/** Function called each tick. */
	public void tick() {
		if( !isActive() )
			return;

		if( this.ticksActive == 0 )
			UndeadArmyText.notifyAboutStart( getNearbyPlayers(), this.direction );

		if( this.ticksActive % 20 == 0 )
			updateUndeadArmyBarVisibility();

		tickCurrentStatus();
		++this.ticksActive;
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
			case FAILED:
				tickFinished();
				break;
			case STOPPED:
				tickStopped();
				break;
		}
	}

	/** Calculates single frame when waiting on next wave. */
	private void tickBetweenWaves() {
		--this.ticksBetweenWaves;
		this.bossInfo.setProgress( Mth.clamp( 1.0f - ( ( float )this.ticksBetweenWaves ) / this.ticksBetweenWavesMaximum, 0.0f, 1.0f ) );

		if( this.ticksBetweenWaves == 0 )
			nextWave();
	}

	/** Calculates single frame when wave is active. */
	private void tickOngoing() {
		this.bossInfo.setProgress( Mth.clamp( 1.0f - ( ( float )this.undeadKilled ) / this.undeadToKill, 0.0f, 1.0f ) );

		if( countNearbyPlayers() == 0 )
			this.status = Status.STOPPED;

		if( !this.spawnerWasCreated && countNearbyUndeadArmy( SAFE_SPAWN_RADIUS / 9.0 ) >= this.undeadToKill / 2 && countNearbyPlayers(
			SAFE_SPAWN_RADIUS / 9.0 ) == 0 )
			createSpawner();

		if( shouldWaveEndPrematurely() )
			killAllUndeadArmyEntities();

		if( this.undeadKilled == this.undeadToKill )
			endWave();

		if( shouldEntitiesBeHighlighted() )
			highlightUndeadArmy();

		++this.ticksWaveActive;
	}

	/** Calculates single frame when Undead Army was finished. (either players win or Undead Army) */
	private void tickFinished() {
		this.ticksBetweenWaves = Math.max( this.ticksBetweenWaves - 1, 0 );

		if( this.ticksBetweenWaves == 0 )
			finish();
	}

	/** Calculates single frame when there is no player nearby Undead Army. */
	private void tickStopped() {
		++this.ticksInactive;

		if( countNearbyPlayers() > 0 )
			this.status = Status.ONGOING;

		if( this.ticksInactive >= this.ticksInactiveMaximum ) {
			this.status = Status.FAILED;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum * 2;
			this.bossInfo.setProgress( 1.0f );
			this.spawnerWasCreated = false;
			createSpawner();
		}
	}

	/** Function called when undead was killed. */
	public void increaseUndeadCounter() {
		this.undeadKilled = Math.min( this.undeadKilled + 1, this.undeadToKill );
	}

	/** Makes all the units from the Undead Army highlighted. */
	public void highlightUndeadArmy() {
		for( Mob monster : getNearbyUndeadArmy( SAFE_SPAWN_RADIUS ) )
			EffectHelper.applyEffectIfPossible( monster, MobEffects.GLOWING, Utility.secondsToTicks( 15.0 ), 5 );
	}

	/** Counts how many entities are left. */
	public int countUndeadEntitiesLeft() {
		return countNearbyUndeadArmy( SAFE_SPAWN_RADIUS );
	}

	/** Updates all nearby undead entities AI goals. Required after world restart. */
	public void updateNearbyUndeadAIGoals() {
		List< Mob > monsters = getNearbyUndeadArmy( SAFE_SPAWN_RADIUS );

		for( Mob monster : monsters )
			updateUndeadAIGoal( monster );
	}

	/** Ends this undead army. */
	public void finish() {
		this.isActive = false;
		this.bossInfo.removeAllPlayers();
	}

	/** Kills all nearby entities from Undead Army. */
	public void killAllUndeadArmyEntities() {
		for( Mob monster : getNearbyUndeadArmy( SAFE_SPAWN_RADIUS ) )
			monster.hurt( DamageSource.MAGIC, 9001 );

		this.undeadKilled = this.undeadToKill;
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
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		if( this.currentWave >= config.getWaves() ) {
			this.status = Status.VICTORY;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum * 2;
			rewardPlayers();
			this.bossInfo.setProgress( 1.0f );
		} else {
			this.status = Status.BETWEEN_WAVES;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum;
		}

		updateBarText();
	}

	/** Creates monster spawner near attacked position. */
	private void createSpawner() {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;
		int spawnerRange = 5;

		for( int i = 0; i < 50 && !this.spawnerWasCreated; i++ ) {
			int x = this.positionToAttack.getX() + MajruszLibrary.RANDOM.nextInt( spawnerRange * 2 + 1 ) - spawnerRange;
			int z = this.positionToAttack.getZ() + MajruszLibrary.RANDOM.nextInt( spawnerRange * 2 + 1 ) - spawnerRange;
			int y = level.getHeight( Heightmap.Types.WORLD_SURFACE, x, z );
			BlockPos position = new BlockPos( x, y, z );

			if( this.level.isEmptyBlock( position ) ) {
				this.level.setBlockAndUpdate( position, Blocks.SPAWNER.defaultBlockState() );
				this.spawnerWasCreated = true;

				SpawnerBlockEntity spawnerEntity = Utility.castIfPossible( SpawnerBlockEntity.class, this.level.getBlockEntity( position ) );
				if( spawnerEntity == null )
					continue;

				spawnerEntity.getSpawner().setEntityId( config.getEntityTypeForMonsterSpawner() );

				this.level.sendParticles( ParticleTypes.SMOKE, x, y, z, 40, 0.5, 0.5, 0.5, 0.01 );
			}
		}

		this.spawnerWasCreated = true;
	}

	/** Spawns monsters depending on current wave. */
	private void spawnWaveEnemies() {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;
		double playersFactor = config.getSizeMultiplier( countNearbyPlayers() );
		this.undeadToKill = 0;
		this.undeadKilled = 0;

		for( WaveMembersConfig.WaveMember waveMember : config.getWaveMembers( this.currentWave ) ) {
			waveMember.amount = ( int )( waveMember.amount * playersFactor );
			for( int i = 0; i < waveMember.amount; i++ ) {
				BlockPos randomPosition = this.direction.getRandomSpawnPosition( this.level, this.positionToAttack, SPAWN_RADIUS );
				Entity entity = waveMember.entityType.create( this.level, null, null, null, randomPosition, MobSpawnType.EVENT, true, true );
				if( !( entity instanceof Mob ) )
					continue;

				Mob monster = ( Mob )entity;
				monster.setPersistenceRequired();
				updateUndeadAIGoal( monster );
				equipWithDyedLeatherArmor( monster );
				tryToEnchantEquipment( monster );
				markAsUndeadArmyUnit( monster );
				if( monster instanceof Skeleton && Random.tryChance( config.getSkeletonHorseChance() ) )
					spawnOnSkeletonHorse( monster );
				monster.setCanPickUpLoot( false );

				if( net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn( monster, this.level, randomPosition.getX(), randomPosition.getY(),
					randomPosition.getZ(), null, MobSpawnType.EVENT
				) )
					continue;
				this.level.addFreshEntity( monster ); // adds monster to the world

				++this.undeadToKill;
			}
		}

		int x = this.positionToAttack.getX() + this.direction.x * SPAWN_RADIUS;
		int z = this.positionToAttack.getZ() + this.direction.z * SPAWN_RADIUS;

		for( ServerPlayer player : getNearbyPlayers() )
			player.connection.send( new ClientboundSoundPacket( Instances.Sounds.UNDEAD_ARMY_WAVE_STARTED, SoundSource.NEUTRAL, x, player.getY(), z, 64.0f, 1.0f, Random.nextInt() ) );

		this.undeadToKill = Math.max( this.undeadToKill, 1 );
	}

	/** Checks whether Undead Army should be highlighted. */
	private boolean shouldEntitiesBeHighlighted() {
		return this.ticksWaveActive >= Utility.minutesToTicks(
			1.5 ) && this.ticksWaveActive % 100 == 0 && this.undeadKilled > this.undeadToKill / 2;
	}

	/** Checks whether wave should be ended earlier. */
	private boolean shouldWaveEndPrematurely() {
		boolean isOnlyFewUndeadLeft = this.undeadKilled >= ( this.undeadToKill * 0.8 ) && countUndeadEntitiesLeft() < 3;
		return isOnlyFewUndeadLeft && this.ticksWaveActive >= Utility.minutesToTicks( 2.5 );
	}

	/** Tries to enchant weapons and armor for given monster. */
	private void tryToEnchantEquipment( Mob monster ) {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;
		double clampedRegionalDifficulty = GameState.getRegionalDifficulty( monster );

		if( monster.hasItemInSlot( EquipmentSlot.MAINHAND ) && Random.tryChance( config.getEnchantedItemChance() ) )
			monster.setItemInHand( InteractionHand.MAIN_HAND, ItemHelper.enchantItem( monster.getMainHandItem(), clampedRegionalDifficulty, false ) );

		for( ItemStack armor : monster.getArmorSlots() )
			if( Random.tryChance( config.getEnchantedItemChance() / 2.0 ) ) {
				armor = ItemHelper.enchantItem( armor, clampedRegionalDifficulty, false );
				if( armor.getEquipmentSlot() != null )
					monster.setItemSlot( armor.getEquipmentSlot(), armor );
			}
	}

	/** Gives a random amount of leather armor to monster. */
	private void equipWithDyedLeatherArmor( Mob monster ) {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;
		double armorPieceChance = config.getArmorPieceChance();
		float armorPieceDropChance = 0.1f;

		equipWithArmorPieceIfPossible( monster, Items.LEATHER_HELMET, "helmet", 1.0 );
		equipWithArmorPieceIfPossible( monster, Items.LEATHER_CHESTPLATE, "chestplate", armorPieceChance );
		equipWithArmorPieceIfPossible( monster, Items.LEATHER_LEGGINGS, "leggings", armorPieceChance );
		equipWithArmorPieceIfPossible( monster, Items.LEATHER_BOOTS, "boots", armorPieceChance );

		monster.setDropChance( EquipmentSlot.FEET, armorPieceDropChance );
		monster.setDropChance( EquipmentSlot.LEGS, armorPieceDropChance );
		monster.setDropChance( EquipmentSlot.CHEST, armorPieceDropChance );
		monster.setDropChance( EquipmentSlot.HEAD, armorPieceDropChance );
	}

	/** Creates new armor piece for undead entity. */
	private void equipWithArmorPieceIfPossible( Mob monster, Item item, String registerName, double chance ) {
		if( Random.tryChance( 1.0 - chance ) || monster instanceof TankEntity )
			return;

		ItemStack armorPiece = new ItemStack( item );
		setUndeadArmyColorAndName( armorPiece, registerName );
		ItemHelper.damageItem( armorPiece, 0.75 );
		monster.equipItemIfPossible( armorPiece );
	}

	/** Changes color of leather armor. */
	private void setUndeadArmyColorAndName( ItemStack armor, String registerName ) {
		CompoundTag nbt = armor.getOrCreateTagElement( "display" );
		nbt.putInt( "color", ARMOR_COLOR );
		nbt.putString( "Name", "{\"translate\":\"majruszsdifficulty.items.undead_" + registerName + "\",\"italic\":false}" );
		armor.addTagElement( "display", nbt );
	}

	/** Rewards all player participating in the raid. */
	private void rewardPlayers() {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		for( Player player : this.level.getPlayers( getParticipantsPredicate() ) ) {
			Vec3 position = player.position();
			for( int i = 0; i < config.getAmountOfVictoryExperience() / 4; i++ )
				this.level.addFreshEntity( new ExperienceOrb( this.level, position.x, position.y + 1, position.z, 4 ) );

			if( player instanceof ServerPlayer )
				Instances.UNDEAD_ARMY_DEFEATED_TRIGGER.trigger( ( ServerPlayer )player, this.currentWave );

			if( Instances.UNDEAD_ARMY_TREASURE_BAG.isAvailable() )
				for( int i = 0; i < config.getAmountOfVictoryTreasureBags(); i++ )
					ItemHelper.giveItemStackToPlayer( new ItemStack( Instances.UNDEAD_ARMY_TREASURE_BAG ), player, this.level );
		}
	}

	/** Saves information about undead army in monster data. */
	private void markAsUndeadArmyUnit( LivingEntity entity ) {
		NBTHelper.saveBlockPos( entity.getPersistentData(), UndeadArmyKeys.POSITION, this.positionToAttack );
	}

	/** Spawns Skeleton Horse and place given monster on them. */
	private void spawnOnSkeletonHorse( Mob monster ) {
		Level level = monster.level;
		SkeletonHorse skeletonHorse = EntityType.SKELETON_HORSE.create( level );
		if( skeletonHorse == null )
			return;

		skeletonHorse.setAge( 0 );
		skeletonHorse.setPos( monster.getX(), monster.getY(), monster.getZ() );
		level.addFreshEntity( skeletonHorse );

		monster.startRiding( skeletonHorse );
		skeletonHorse.goalSelector.addGoal( 4, new UndeadAttackPositionGoal( skeletonHorse, getAttackPosition(), 1.5f, 20.0f, 3.0f ) );
		markAsUndeadArmyUnit( skeletonHorse );
	}

	/** Adds Undead Army AI goal for given monster. */
	private void updateUndeadAIGoal( Mob monster ) {
		float speedModifier = monster instanceof TankEntity ? 1.5f : 1.25f;
		monster.goalSelector.addGoal( 4, new UndeadAttackPositionGoal( monster, getAttackPosition(), speedModifier, 20.0f, 3.0f ) );

		PathfinderMob pathfinderMob = Utility.castIfPossible( PathfinderMob.class, monster );
		if( pathfinderMob != null ) {
			monster.targetSelector.getAvailableGoals().removeIf( wrappedGoal->wrappedGoal.getGoal() instanceof HurtByTargetGoal );
			monster.targetSelector.addGoal( 1, new ForgiveUndeadArmyTargetGoal( pathfinderMob ) );
		}
	}

	/** Updates visibility of Undead Army progress bar for nearby players. */
	private void updateUndeadArmyBarVisibility() {
		Set< ServerPlayer > currentPlayers = Sets.newHashSet( this.bossInfo.getPlayers() );
		List< ServerPlayer > validPlayers = getNearbyPlayers();

		for( ServerPlayer player : validPlayers )
			if( !currentPlayers.contains( player ) )
				this.bossInfo.addPlayer( player );

		for( ServerPlayer player : currentPlayers )
			if( !validPlayers.contains( player ) )
				this.bossInfo.removePlayer( player );
	}

	private AABB getAxisAligned( double range ) {
		Vec3i vector = new Vec3i( range, range, range );

		return new AABB( getAttackPosition().subtract( vector ), getAttackPosition().offset( vector ) );
	}

	/** Predicate for checking whether given monster entity is alive and belongs to the Undead Army. */
	private Predicate< Mob > getUndeadParticipantsPredicate() {
		return monster->( monster.isAlive() && doesEntityBelongToUndeadArmy( monster ) );
	}

	/** Returns list of nearby Undead Army units. */
	private List< Mob > getNearbyUndeadArmy( double range ) {
		return this.level.getEntitiesOfClass( Mob.class, getAxisAligned( range ), getUndeadParticipantsPredicate() );
	}

	/** Returns amount of nearby Undead Army units. */
	private int countNearbyUndeadArmy( double range ) {
		return getNearbyUndeadArmy( range ).size();
	}

	/** Predicate for checking whether given player is alive and is participating in the raid. */
	private Predicate< ServerPlayer > getParticipantsPredicate() {
		return player->player.isAlive() && RegistryHandler.UNDEAD_ARMY_MANAGER != null && ( RegistryHandler.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy(
			new BlockPos( player.position() ) ) == this
		);
	}

	/** Returns list of nearby players. */
	private List< ServerPlayer > getNearbyPlayers( double range ) {
		return this.level.getEntitiesOfClass( ServerPlayer.class, getAxisAligned( range ), getParticipantsPredicate() );
	}

	/** Returns list of nearby players participating in the raid. */
	private List< ServerPlayer > getNearbyPlayers() {
		return this.level.getPlayers( getParticipantsPredicate() );
	}

	/** Returns amount of nearby players participating in the raid. */
	private int countNearbyPlayers( double range ) {
		return getNearbyPlayers( range ).size();
	}

	/** Returns amount of nearby players participating in the raid. */
	private int countNearbyPlayers() {
		return getNearbyPlayers().size();
	}
}

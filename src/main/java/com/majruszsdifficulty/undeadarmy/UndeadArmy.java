package com.majruszsdifficulty.undeadarmy;

import com.google.common.collect.Sets;
import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.goals.ForgiveUndeadArmyTargetGoal;
import com.majruszsdifficulty.goals.UndeadAttackPositionGoal;
import com.majruszsdifficulty.items.UndeadArmorItem;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.items.ItemHelper;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.nbt.NBTHelper;
import com.mlib.time.TimeHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class UndeadArmy {
	private final static int SAFE_SPAWN_RADIUS = 90;
	private final static int SPAWN_RADIUS = 70;
	private final ServerLevel level;
	private final ServerBossEvent bossInfo = new ServerBossEvent( UndeadArmyText.TITLE, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 );
	private final List< Pair< BlockPos, EntityType< ? > > > spawnInfoList = new ArrayList<>();
	private final BlockPos positionToAttack;
	private final Direction direction;
	private Status status;
	private boolean isActive;
	private int ticksActive;
	private int ticksInactive;
	private final int ticksInactiveMaximum;
	private int ticksWaveActive;
	private int ticksBetweenWaves;
	private final int ticksBetweenWavesMaximum;
	private int currentWave;
	private int undeadToKill;
	private int undeadKilled;

	public UndeadArmy( ServerLevel level, BlockPos positionToAttack, Direction direction ) {
		this.level = level;
		this.positionToAttack = positionToAttack;
		this.direction = direction;
		this.status = Status.BETWEEN_WAVES;
		this.isActive = true;
		this.ticksActive = 0;
		this.ticksInactive = 0;
		this.ticksWaveActive = 0;
		this.ticksBetweenWaves = 0;
		this.currentWave = 0;
		this.undeadToKill = 1;
		this.undeadKilled = 0;
		this.ticksBetweenWaves = this.ticksBetweenWavesMaximum = UndeadArmyConfig.getTicksBetweenWaves();
		this.ticksInactiveMaximum = UndeadArmyConfig.getInactivityTicks();

		this.bossInfo.setProgress( 0.0f );
		generateSpawnInfo();
	}

	public UndeadArmy( ServerLevel level, CompoundTag nbt ) {
		this.level = level;
		this.positionToAttack = NBTHelper.loadBlockPos( nbt, UndeadArmyKeys.POSITION );
		this.direction = Direction.getByName( nbt.getString( UndeadArmyKeys.DIRECTION ) );
		this.status = Status.getByName( nbt.getString( UndeadArmyKeys.STATUS ) );
		this.isActive = nbt.getBoolean( UndeadArmyKeys.ACTIVE );
		this.ticksActive = nbt.getInt( UndeadArmyKeys.TICKS_ACTIVE );
		this.ticksInactive = nbt.getInt( UndeadArmyKeys.TICKS_INACTIVE );
		this.ticksWaveActive = nbt.getInt( UndeadArmyKeys.TICKS_WAVE );
		this.ticksBetweenWaves = nbt.getInt( UndeadArmyKeys.TICKS_BETWEEN );
		this.currentWave = nbt.getInt( UndeadArmyKeys.WAVE );
		this.undeadToKill = nbt.getInt( UndeadArmyKeys.TO_KILL );
		this.undeadKilled = nbt.getInt( UndeadArmyKeys.KILLED );
		this.ticksBetweenWaves = this.ticksBetweenWavesMaximum = UndeadArmyConfig.getTicksBetweenWaves();
		this.ticksInactiveMaximum = UndeadArmyConfig.getInactivityTicks();

		updateProgressBarText();
	}

	public CompoundTag write( CompoundTag nbt ) {
		NBTHelper.saveBlockPos( nbt, UndeadArmyKeys.POSITION, this.positionToAttack );
		nbt.putString( UndeadArmyKeys.DIRECTION, String.valueOf( this.direction ) );
		nbt.putString( UndeadArmyKeys.STATUS, String.valueOf( this.status ) );
		nbt.putBoolean( UndeadArmyKeys.ACTIVE, this.isActive );
		nbt.putInt( UndeadArmyKeys.TICKS_ACTIVE, this.ticksActive );
		nbt.putInt( UndeadArmyKeys.TICKS_INACTIVE, this.ticksInactive );
		nbt.putInt( UndeadArmyKeys.TICKS_WAVE, this.ticksWaveActive );
		nbt.putInt( UndeadArmyKeys.TICKS_BETWEEN, this.ticksBetweenWaves );
		nbt.putInt( UndeadArmyKeys.WAVE, this.currentWave );
		nbt.putInt( UndeadArmyKeys.TO_KILL, this.undeadToKill );
		nbt.putInt( UndeadArmyKeys.KILLED, this.undeadKilled );

		return nbt;
	}

	public BlockPos getAttackedPosition() {
		return this.positionToAttack;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public boolean hasEnded() {
		return !this.isActive;
	}

	public void updateProgressBarText() {
		switch( this.status ) {
			case ONGOING -> this.bossInfo.setName( this.currentWave == 0 ? UndeadArmyText.TITLE : UndeadArmyText.constructWaveMessage( this.currentWave ) );
			case BETWEEN_WAVES -> this.bossInfo.setName( UndeadArmyText.BETWEEN_WAVES );
			case VICTORY -> this.bossInfo.setName( UndeadArmyText.VICTORY );
			case FAILED -> this.bossInfo.setName( UndeadArmyText.FAILED );
		}
	}

	public void tick() {
		if( hasEnded() )
			return;

		if( this.ticksActive++ == 0 ) {
			MutableComponent message = UndeadArmyText.constructDirectionMessage( this.direction );
			List< ServerPlayer > players = getNearbyPlayers();
			players.forEach( player->player.displayClientMessage( message, false ) );
		}
		if( TimeHelper.hasServerSecondsPassed( 1.0 ) ) {
			updateUndeadArmyBarVisibility();
		}
		switch( this.status ) {
			case BETWEEN_WAVES -> tickBetweenWaves();
			case ONGOING -> tickOngoing();
			case VICTORY, FAILED -> tickFinished();
			case STOPPED -> tickStopped();
		}
	}

	public void increaseUndeadCounter() {
		this.undeadKilled = Math.min( this.undeadKilled + 1, this.undeadToKill );
	}

	public void highlightArmy() {
		for( Mob monster : getArmyMobs() ) {
			MobEffectHelper.tryToApply( monster, MobEffects.GLOWING, Utility.secondsToTicks( 15.0 ), 5 );
		}
	}

	public int countMobsLeft() {
		return countArmyMobs();
	}

	public void finish() {
		this.isActive = false;
		this.bossInfo.removeAllPlayers();
	}

	public void killAllUndeadArmyMobs() {
		this.undeadKilled = this.undeadToKill;

		List< Mob > mobs = getArmyMobs();
		mobs.forEach( mob->mob.hurt( DamageSource.MAGIC, 9001 ) );
	}

	public void addUndeadArmyAI( Mob monster ) {
		float speedModifier = monster instanceof TankEntity ? 1.5f : 1.25f;
		monster.goalSelector.addGoal( 4, new UndeadAttackPositionGoal( monster, getAttackedPosition(), speedModifier, 20.0f, 3.0f ) );

		PathfinderMob pathfinderMob = Utility.castIfPossible( PathfinderMob.class, monster );
		if( pathfinderMob != null ) {
			monster.targetSelector.getAvailableGoals().removeIf( wrappedGoal->wrappedGoal.getGoal() instanceof HurtByTargetGoal );
			monster.targetSelector.addGoal( 1, new ForgiveUndeadArmyTargetGoal( pathfinderMob ) );
		}
	}

	private void tickBetweenWaves() {
		if( this.spawnInfoList.isEmpty() ) {
			generateSpawnInfo();
		}

		if( this.ticksBetweenWaves-- <= 0 )
			proceedToNextWave();

		this.bossInfo.setProgress( Mth.clamp( 1.0f - ( ( float )this.ticksBetweenWaves ) / this.ticksBetweenWavesMaximum, 0.0f, 1.0f ) );
		this.spawnInfoList.forEach( spawnInfo->{
			BlockPos position = spawnInfo.getFirst();
			this.level.sendParticles( ParticleTypes.SOUL, position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 2, 0.5, 0.5, 0.5, 0.02 );
		} );
	}

	private void tickOngoing() {
		if( countNearbyPlayers() == 0 )
			this.status = Status.STOPPED;

		if( this.undeadKilled == this.undeadToKill )
			endWave();

		if( shouldMobsBeHighlighted() )
			highlightArmy();

		if( ++this.ticksWaveActive % 20 == 0 ) {
			this.undeadKilled = this.undeadToKill - this.countArmyMobs();
			// sometimes mobs can go outside the raid or turn into Drowned and this
			// value may become invalid, so to make sure this does not break the raid
			// there is an update every second to keep the value up to date
		}

		this.bossInfo.setProgress( Mth.clamp( 1.0f - ( ( float )this.undeadKilled ) / this.undeadToKill, 0.0f, 1.0f ) );
	}

	private void tickFinished() {
		if( --this.ticksBetweenWaves <= 0 )
			finish();
	}

	private void tickStopped() {
		if( countNearbyPlayers() > 0 )
			this.status = Status.ONGOING;

		if( this.ticksInactive++ >= this.ticksInactiveMaximum ) {
			this.status = Status.FAILED;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum * 2;
			this.bossInfo.setProgress( 1.0f );
		}
	}

	private void proceedToNextWave() {
		++this.currentWave;
		this.status = Status.ONGOING;
		this.ticksWaveActive = 0;
		spawnWaveEnemies();
		updateProgressBarText();
	}

	private void endWave() {
		if( this.currentWave >= UndeadArmyConfig.getWavesCount() ) {
			this.status = Status.VICTORY;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum * 2;
			rewardPlayers();
			this.bossInfo.setProgress( 1.0f );
		} else {
			this.status = Status.BETWEEN_WAVES;
			this.ticksBetweenWaves = this.ticksBetweenWavesMaximum;
			generateSpawnInfo();
		}

		updateProgressBarText();
		awardAdvancements();
	}

	private void generateSpawnInfo() {
		this.spawnInfoList.clear();
		UndeadArmyConfig.getWaveMembers( this.currentWave + 1 ).forEach( waveMember->{
			int totalAmount = this.getTotalAmount( waveMember );
			for( int i = 0; i < totalAmount; i++ ) {
				this.spawnInfoList.add( new Pair<>( this.direction.getRandomSpawnPosition( this.level, this.positionToAttack, SPAWN_RADIUS ), waveMember.entityType() ) );
			}
		} );
	}

	private int getTotalAmount( WaveMembersConfig.WaveMember waveMember ) {
		return ( int )( waveMember.amount() * UndeadArmyConfig.getSizeMultiplier( countNearbyPlayers() ) );
	}

	private void spawnWaveEnemies() {
		this.undeadToKill = 0;
		this.undeadKilled = 0;
		for( Pair< BlockPos, EntityType< ? > > spawnInfo : this.spawnInfoList ) {
			BlockPos randomPosition = spawnInfo.getFirst();
			EntityType< ? > entityType = spawnInfo.getSecond();
			Entity entity = entityType.create( this.level, null, null, null, randomPosition, MobSpawnType.EVENT, true, true );
			if( !( entity instanceof Mob monster ) )
				continue;

			monster.setPersistenceRequired();
			addUndeadArmyAI( monster );
			equipWithUndeadArmyArmor( monster );
			tryToEnchantEquipment( monster );
			markAsUndeadArmyMob( monster );
			if( monster instanceof Skeleton && Random.tryChance( UndeadArmyConfig.getSkeletonHorseChance() ) )
				spawnOnSkeletonHorse( monster );
			monster.setCanPickUpLoot( false );

			if( net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn( monster, this.level, randomPosition.getX(), randomPosition.getY(), randomPosition.getZ(), null, MobSpawnType.EVENT ) )
				continue;
			this.level.addFreshEntity( monster );

			++this.undeadToKill;
		}

		int x = this.positionToAttack.getX() + this.direction.x * SPAWN_RADIUS;
		int z = this.positionToAttack.getZ() + this.direction.z * SPAWN_RADIUS;

		for( ServerPlayer player : getNearbyPlayers() )
			player.connection.send( new ClientboundSoundPacket( Registries.UNDEAD_ARMY_WAVE_STARTED.getHolder().get().value(), SoundSource.NEUTRAL, x, player.getY(), z, 64.0f, 1.0f ) );

		this.undeadToKill = Math.max( this.undeadToKill, 1 );
	}

	private boolean shouldMobsBeHighlighted() {
		return this.ticksWaveActive >= Utility.minutesToTicks( 1.5 ) && this.ticksWaveActive % 100 == 0 && this.undeadKilled > this.undeadToKill / 2;
	}

	private void tryToEnchantEquipment( Mob monster ) {
		double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( monster );
		double chanceToEnchant = UndeadArmyConfig.getEnchantedItemChance();
		if( monster.hasItemInSlot( EquipmentSlot.MAINHAND ) && Random.tryChance( chanceToEnchant ) )
			monster.setItemInHand( InteractionHand.MAIN_HAND, ItemHelper.enchantItem( monster.getMainHandItem(), clampedRegionalDifficulty, false ) );

		for( ItemStack armor : monster.getArmorSlots() )
			if( Random.tryChance( chanceToEnchant / 2.0 ) ) {
				armor = ItemHelper.enchantItem( armor, clampedRegionalDifficulty, false );
				if( armor.getEquipmentSlot() != null )
					monster.setItemSlot( armor.getEquipmentSlot(), armor );
			}
	}

	public static void equipWithUndeadArmyArmor( Mob monster ) {
		float chanceToEquip = ( float )UndeadArmyConfig.getArmorPieceChance();
		float chanceToDrop = 0.015f;

		tryToEquipItem( monster, UndeadArmorItem.HELMET_ID, 1.0f, chanceToDrop );
		tryToEquipItem( monster, UndeadArmorItem.CHESTPLATE_ID, chanceToEquip, chanceToDrop );
		tryToEquipItem( monster, UndeadArmorItem.LEGGINGS_ID, chanceToEquip, chanceToDrop );
		tryToEquipItem( monster, UndeadArmorItem.BOOTS_ID, chanceToEquip, chanceToDrop );
	}

	private static void tryToEquipItem( Mob monster, String itemId, float chanceToEquip, float chanceToDrop ) {
		if( Random.tryChance( 1.0 - chanceToEquip ) || monster instanceof TankEntity )
			return;

		UndeadArmorItem.ItemData itemData = UndeadArmorItem.getData( itemId );
		ItemStack armorPiece = UndeadArmorItem.constructItem( itemId );
		ItemHelper.damageItem( armorPiece, 0.75 );
		monster.equipItemIfPossible( armorPiece );
		monster.setDropChance( itemData.slot(), chanceToDrop );
	}

	private void rewardPlayers() {
		for( ServerPlayer player : this.level.getPlayers( getParticipantsPredicate() ) ) {
			Vec3 position = player.position();
			for( int i = 0; i < UndeadArmyConfig.getAmountOfVictoryExperience() / 4; i++ )
				this.level.addFreshEntity( new ExperienceOrb( this.level, position.x, position.y + 1, position.z, 4 ) );

			if( Registries.UNDEAD_ARMY_TREASURE_BAG.get().isEnabled() )
				ItemHelper.giveItemStackToPlayer( new ItemStack( Registries.UNDEAD_ARMY_TREASURE_BAG.get() ), player, this.level );
		}
	}

	private void markAsUndeadArmyMob( LivingEntity entity ) {
		NBTHelper.saveBlockPos( entity.getPersistentData(), UndeadArmyKeys.POSITION, this.positionToAttack );
	}

	public static void markAsUndeadArmyPatrol( LivingEntity entity ) {
		entity.getPersistentData().putBoolean( UndeadArmyKeys.PATROL, true );
	}

	private void spawnOnSkeletonHorse( Mob monster ) {
		Level level = monster.level;
		SkeletonHorse skeletonHorse = EntityType.SKELETON_HORSE.create( level );
		if( skeletonHorse == null )
			return;

		skeletonHorse.setAge( 0 );
		skeletonHorse.setPos( monster.getX(), monster.getY(), monster.getZ() );
		level.addFreshEntity( skeletonHorse );

		monster.startRiding( skeletonHorse );
		skeletonHorse.goalSelector.addGoal( 4, new UndeadAttackPositionGoal( skeletonHorse, getAttackedPosition(), 1.5f, 20.0f, 3.0f ) );
		markAsUndeadArmyMob( skeletonHorse );
	}

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

	private void awardAdvancements() {
		for( ServerPlayer player : this.level.getPlayers( getParticipantsPredicate() ) ) {
			Registries.UNDEAD_ARMY_DEFEATED_TRIGGER.trigger( player, this.currentWave );
		}
	}

	private AABB getAxisAligned( double range ) {
		Vec3i vector = new Vec3i( range, range, range );
		return new AABB( getAttackedPosition().subtract( vector ), getAttackedPosition().offset( vector ) );
	}

	private Predicate< Mob > getUndeadParticipantsPredicate() {
		return mob->( mob.isAlive() && UndeadArmyManager.belongsToUndeadArmy( mob ) );
	}

	private List< Mob > getArmyMobs() {
		return this.level.getEntitiesOfClass( Mob.class, getAxisAligned( UndeadArmy.SAFE_SPAWN_RADIUS ), getUndeadParticipantsPredicate() );
	}

	private int countArmyMobs() {
		return getArmyMobs().size();
	}

	private Predicate< ServerPlayer > getParticipantsPredicate() {
		return player->player.isAlive() && Registries.UNDEAD_ARMY_MANAGER != null && Registries.UNDEAD_ARMY_MANAGER.findNearestUndeadArmy( new BlockPos( player.position() ) ) == this;
	}

	private List< ServerPlayer > getNearbyPlayers( double range ) {
		return this.level.getEntitiesOfClass( ServerPlayer.class, getAxisAligned( range ), getParticipantsPredicate() );
	}

	private List< ServerPlayer > getNearbyPlayers() {
		return this.level.getPlayers( getParticipantsPredicate() );
	}

	private int countNearbyPlayers( double range ) {
		return getNearbyPlayers( range ).size();
	}

	private int countNearbyPlayers() {
		return getNearbyPlayers().size();
	}
}

package com.majruszs_difficulty.events;

import com.google.common.collect.Sets;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class UndeadArmy {
	public static class Texts {
		public IFormattableTextComponent title = new TranslationTextComponent( "majruszs_difficulty.undead_army.title" ).func_240699_a_(
			TextFormatting.BOLD );
	}

	private final Texts texts = new Texts();
	private final ServerBossInfo bossInfo = new ServerBossInfo( texts.title, BossInfo.Color.WHITE, BossInfo.Overlay.NOTCHED_12 );
	private final int maximumDistanceToArmy = 9001;
	private ServerWorld world;
	private long ticksActive = 0;
	private boolean isActive = true;
	private BlockPos positionToAttack;

	public UndeadArmy( ServerWorld world, BlockPos positionToAttack ) {
		this.world = world;
		this.positionToAttack = positionToAttack;
	}

	public UndeadArmy( ServerWorld world, CompoundNBT nbt ) {
		this.world = world;
		this.ticksActive = nbt.getLong( "TicksActive" );
		this.isActive = nbt.getBoolean( "IsActive" );
		this.positionToAttack = new BlockPos( nbt.getInt( "PositionX" ), nbt.getInt( "PositionY" ), nbt.getInt( "PositionZ" ) );

		this.bossInfo.removeAllPlayers();
	}

	public CompoundNBT write( CompoundNBT nbt ) {
		nbt.putLong( "TicksActive", this.ticksActive );
		nbt.putBoolean( "IsActive", this.isActive );
		nbt.putInt( "PositionX", this.positionToAttack.getX() );
		nbt.putInt( "PositionY", this.positionToAttack.getY() );
		nbt.putInt( "PositionZ", this.positionToAttack.getZ() );

		return nbt;
	}

	public BlockPos getPosition() {
		return this.positionToAttack;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void tick() {
		if( !this.isActive )
			return;

		this.ticksActive++;

		if( this.ticksActive % 20L == 0L )
			updateUndeadArmyBarVisibility();

		if( this.ticksActive % 200L == 0L ) {
			this.isActive = false;
			this.bossInfo.removeAllPlayers();
		}
	}

	public void updateWorld( ServerWorld world ) {
		this.world = world;
	}

	private Predicate< ServerPlayerEntity > getParticipantsPredicate() {
		return player -> {
			BlockPos position = new BlockPos( player.getPositionVec() );
			return player.isAlive() && ( RegistryHandler.undeadArmyManager.findUndeadArmy( position, this.maximumDistanceToArmy ) == this );
		};
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

	private float getEnchantmentOdds() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 0.125f;
			case EXPERT:
				return 0.25f;
			case MASTER:
				return 0.5f;
		}
	}

	public enum WaveMember implements IExtensibleEnum {
		ZOMBIE( EntityType.ZOMBIE, new int[]{ 5, 5, 5, 5, 5 } ), HUSK( EntityType.HUSK, new int[]{ 2, 2, 2, 2, 2 } ), SKELETON( EntityType.SKELETON,
			new int[]{ 2, 2, 2, 2, 2 }
		), STRAY( EntityType.STRAY, new int[]{ 2, 2, 2, 2, 2 } ), ELITE_SKELETON( EliteSkeletonEntity.type, new int[]{ 5, 5, 5, 5, 5 } );

		private static WaveMember[] VALUES = values();
		private final EntityType< ? > type;
		private final int[] waveCounts;

		WaveMember( EntityType< ? > type, int[] waveCounts ) {
			this.type = type;
			this.waveCounts = waveCounts;
		}
	}

	public enum Direction {
		WEST( -1, 0 ), EAST( 1, 0 ), NORTH( 0, 1 ), SOUTH( 0, -1 );

		public final int x, y;

		Direction( int x, int y ) {
			this.x = x;
			this.y = y;
		}
	}
}

package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.events.UndeadArmy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class UndeadArmyManager extends WorldSavedData {
	public static final String DATA_NAME = "undead_army";
	private ServerWorld world;
	private final List< UndeadArmy > undeadArmies = new ArrayList<>();
	private long ticksActive = 0L;

	public UndeadArmyManager( ServerWorld world ) {
		super( DATA_NAME );

		this.world = world;
	}

	@Override
	public void read( CompoundNBT nbt ) {
		this.ticksActive = nbt.getLong( "TicksActive" );
		ListNBT listNBT = nbt.getList( "Armies", 10 );

		for( int i = 0; i < listNBT.size(); i++ )
			this.undeadArmies.add( new UndeadArmy( this.world, listNBT.getCompound( i ) ) );
	}

	@Override
	public CompoundNBT write( CompoundNBT compoundNBT ) {
		compoundNBT.putLong( "Tick", this.ticksActive );
		ListNBT listNBT = new ListNBT();

		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			CompoundNBT nbt = new CompoundNBT();
			undeadArmy.write( nbt );
			listNBT.add( nbt );
		}

		compoundNBT.put( "Armies", listNBT );
		return compoundNBT;
	}

	public void updateWorld( ServerWorld world ) {
		this.world = world;

		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateWorld( world );
	}

	public boolean spawn( PlayerEntity player, ServerWorld world ) {
		this.undeadArmies.add( new UndeadArmy( world, new BlockPos( player.getPositionVec() ) ) );

		// for( UndeadArmy undeadArmy : this.undeadArmies )
			// MajruszsDifficulty.LOGGER.info( undeadArmy.world.getPlayers() );
		return true;
	}

	public void tick() {
		this.ticksActive++;

		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.tick();

		if( this.ticksActive % 20L == 0L ) {
			this.undeadArmies.removeIf( undeadArmy->!undeadArmy.isActive() );
			MajruszsDifficulty.LOGGER.info( this.ticksActive );
		}

		if( this.ticksActive % 200L == 0L )
			this.markDirty();
	}

	public IFormattableTextComponent getFailedMessage() {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
		message.func_240699_a_( TextFormatting.RED );

		return message;
	}

	@Nullable
	public UndeadArmy findUndeadArmy( BlockPos position, int maxDistanceToArmy ) {
		UndeadArmy undeadArmy = null;
		double maxDistance = ( double )maxDistanceToArmy;
		for( UndeadArmy current : this.undeadArmies ) {
			double distance = current.getPosition().distanceSq( position );

			if( /*current.isActive()*/ distance < maxDistance ) {
				undeadArmy = current;
				maxDistance = distance;
			}
		}

		return undeadArmy;
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.END )
			return;

		RegistryHandler.undeadArmyManager.tick();
	}

	/*public static boolean spawn( PlayerEntity player, ServerWorld world, Direction direction ) {
		boolean isPlayerInOverworld = player.world.func_230315_m_()
			.func_242725_p()
			.equals( DimensionType.field_235999_c_.func_240901_a_() );

		if( isActive || world.isDaytime() || !isPlayerInOverworld )
			return false;

		UndeadArmyManager.fromDirection = direction;
		UndeadArmyManager.ticksToStart = MajruszsHelper.secondsToTicks( 6.5 );
		UndeadArmyManager.isActive = true;
		UndeadArmyManager.world = world;
		UndeadArmyManager.attackPosition = player.getBedPosition()
			.orElse( new BlockPos( player.getPositionVec() ) );

		world.playSound( null, attackPosition, RegistryHandler.UNDEAD_ARMY_APPROACHING.get(), SoundCategory.AMBIENT, 0.25f, 1.0f );
		MajruszsDifficulty.LOGGER.info( "Spawned undead army!" );

		return true;
	}

	public static boolean spawn( PlayerEntity player, ServerWorld world ) {
		return spawn( player, world, Direction.values()[ MajruszsDifficulty.RANDOM.nextInt( Direction.values().length ) ] );
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.START )
			return;

		if( ticksToStart > 0 ) {
			if( ticksToStart == 1 )
				notifyAllPlayers();

			ticksToStart--;
		} else
			isActive = false;
	}

	public static IFormattableTextComponent getFailedMessage() {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
		message.func_240699_a_( TextFormatting.RED );

		return message;
	}

	private static void notifyAllPlayers() {
		IFormattableTextComponent message = getMessage( fromDirection );
		for( PlayerEntity player : world.getPlayers( player->player.getDistanceSq( Vector3d.func_237489_a_( attackPosition ) ) < ( 100 * 100 ) ) )
			player.sendStatusMessage( message, false );
	}

	private static IFormattableTextComponent getMessage( Direction direction ) {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.approaching" );
		message.func_230529_a_( new StringTextComponent( " " ) );
		message.func_230529_a_( new TranslationTextComponent( "majruszs_difficulty.undead_army." + direction.toString()
			.toLowerCase() ) );
		message.func_230529_a_( new StringTextComponent( "!" ) );
		message.func_240699_a_( TextFormatting.BOLD );
		message.func_240699_a_( TextFormatting.DARK_PURPLE );

		return message;
	}*/
}













/*
@Mod.EventBusSubscriber
public class UndeadArmyManager extends WorldSavedData {
	protected static boolean isActive = false;
	protected static int ticksToStart = 0;
	protected static Direction fromDirection;
	protected static ServerWorld world;
	protected static BlockPos attackPosition;

	public UndeadArmyManager( String name ) {
		super( name );
	}

	public static boolean spawn( PlayerEntity player, ServerWorld world, Direction direction ) {
		boolean isPlayerInOverworld = player.world.func_230315_m_()
			.func_242725_p()
			.equals( DimensionType.field_235999_c_.func_240901_a_() );

		if( isActive || world.isDaytime() || !isPlayerInOverworld )
			return false;

		UndeadArmyManager.fromDirection = direction;
		UndeadArmyManager.ticksToStart = MajruszsHelper.secondsToTicks( 6.5 );
		UndeadArmyManager.isActive = true;
		UndeadArmyManager.world = world;
		UndeadArmyManager.attackPosition = player.getBedPosition()
			.orElse( new BlockPos( player.getPositionVec() ) );

		world.playSound( null, attackPosition, RegistryHandler.UNDEAD_ARMY_APPROACHING.get(), SoundCategory.AMBIENT, 0.25f, 1.0f );
		MajruszsDifficulty.LOGGER.info( "Spawned undead army!" );

		return true;
	}

	public static boolean spawn( PlayerEntity player, ServerWorld world ) {
		return spawn( player, world, Direction.values()[ MajruszsDifficulty.RANDOM.nextInt( Direction.values().length ) ] );
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.START )
			return;

		if( ticksToStart > 0 ) {
			if( ticksToStart == 1 )
				notifyAllPlayers();

			ticksToStart--;
		} else
			isActive = false;
	}

	public static IFormattableTextComponent getFailedMessage() {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
		message.func_240699_a_( TextFormatting.RED );

		return message;
	}

	private static void notifyAllPlayers() {
		IFormattableTextComponent message = getMessage( fromDirection );
		for( PlayerEntity player : world.getPlayers( player->player.getDistanceSq( Vector3d.func_237489_a_( attackPosition ) ) < ( 100 * 100 ) ) )
			player.sendStatusMessage( message, false );
	}

	private static IFormattableTextComponent getMessage( Direction direction ) {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.approaching" );
		message.func_230529_a_( new StringTextComponent( " " ) );
		message.func_230529_a_( new TranslationTextComponent( "majruszs_difficulty.undead_army." + direction.toString()
			.toLowerCase() ) );
		message.func_230529_a_( new StringTextComponent( "!" ) );
		message.func_240699_a_( TextFormatting.BOLD );
		message.func_240699_a_( TextFormatting.DARK_PURPLE );

		return message;
	}

	@Override
	public void read( CompoundNBT nbt ) {

	}

	@Override
	public CompoundNBT write( CompoundNBT compound ) {
		return null;
	}

	public enum Direction {
		WEST, EAST, NORTH, SOUTH
	}
}
*/
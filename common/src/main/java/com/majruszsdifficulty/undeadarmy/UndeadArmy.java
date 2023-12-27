package com.majruszsdifficulty.undeadarmy;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.undeadarmy.events.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class UndeadArmy {
	public final ServerBossEvent waveInfo = ( ServerBossEvent )new ServerBossEvent( TextHelper.empty(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10 ).setCreateWorldFog( true );
	public final ServerBossEvent bossInfo = new ServerBossEvent( TextHelper.empty(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6 );
	public final List< ServerPlayer > participants = new ArrayList<>();
	public List< MobInfo > mobsLeft = new ArrayList<>();
	public GameStage gameStage;
	public BlockPos position;
	public Direction direction;
	public Phase phase = new Phase();
	public int currentWave = 0;
	public Entity boss = null;
	public boolean areEntitiesLoaded = false;

	static {
		Serializables.get( UndeadArmy.class )
			.define( "mobs_left", Reader.list( Reader.custom( MobInfo::new ) ), s->s.mobsLeft, ( s, v )->s.mobsLeft = v )
			.define( "game_stage", Reader.string(), s->s.gameStage.getId(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.define( "position", Reader.blockPos(), s->s.position, ( s, v )->s.position = v )
			.define( "direction", Reader.enumeration( Direction::values ), s->s.direction, ( s, v )->s.direction = v )
			.define( "phase", Reader.custom( Phase::new ), s->s.phase, ( s, v )->s.phase = v )
			.define( "current_wave", Reader.integer(), s->s.currentWave, ( s, v )->s.currentWave = v );

		Serializables.get( Phase.class )
			.define( "state", Reader.enumeration( Phase.State::values ), s->s.state, ( s, v )->s.state = v )
			.define( "ticks_left", Reader.integer(), s->s.ticksLeft, ( s, v )->s.ticksLeft = v )
			.define( "ticks_total", Reader.integer(), s->s.ticksTotal, ( s, v )->s.ticksTotal = v )
			.define( "health_total", Reader.integer(), s->s.healthTotal, ( s, v )->s.healthTotal = v );

		Serializables.get( MobInfo.class )
			.define( "type", Reader.entityType(), s->s.type, ( s, v )->s.type = v )
			.define( "equipment", Reader.optional( Reader.location() ), s->s.equipment, ( s, v )->s.equipment = v )
			.define( "position", Reader.blockPos(), s->s.position, ( s, v )->s.position = v )
			.define( "is_boss", Reader.bool(), s->s.isBoss, ( s, v )->s.isBoss = v )
			.define( "uuid", Reader.optional( Reader.uuid() ), s->s.uuid, ( s, v )->s.uuid = v );
	}

	public void start( BlockPos position, Direction direction ) {
		this.gameStage = GameStageHelper.determineGameStage( this.getLevel(), position.getCenter() );
		this.position = position;
		this.direction = direction;
		this.areEntitiesLoaded = true;
		this.setState( Phase.State.STARTED, 6.4f );

		Events.dispatch( new OnUndeadArmyStarted( this ) );
	}

	public void finish() {
		this.setState( Phase.State.FINISHED, 0.0f );
	}

	public void tick() {
		if( !this.areEntitiesLoaded ) {
			this.areEntitiesLoaded = this.mobsLeft.stream().allMatch( mobInfo->mobInfo.uuid == null || EntityHelper.isLoaded( this.getLevel(), mobInfo.uuid ) );
			if( this.areEntitiesLoaded ) {
				Events.dispatch( new OnUndeadArmyLoaded( this ) );
			} else {
				return;
			}
		}

		if( this.getLevel().getDifficulty() == Difficulty.PEACEFUL ) {
			this.finish();
			return;
		}

		Events.dispatch( new OnUndeadArmyTicked( this ) );
	}

	public void highlight() {
		this.forEachSpawnedUndead( entity->entity.addEffect( new MobEffectInstance( MobEffects.GLOWING, TimeHelper.toTicks( 15.0 ), 0 ) ) );
	}

	public void killAllMobs() {
		this.forEachSpawnedUndead( Entity::kill );
		this.mobsLeft.clear();
	}

	public void setState( Phase.State state, float durationLeft ) {
		this.phase.state = state;
		this.phase.ticksLeft = TimeHelper.toTicks( durationLeft );
		this.phase.ticksTotal = Math.max( this.phase.ticksLeft, 1 );

		Events.dispatch( new OnUndeadArmyStateChanged( this ) );
		if( this.phase.state == Phase.State.WAVE_PREPARING && this.currentWave > 0 ) {
			Events.dispatch( new OnUndeadArmyWaveFinished( this ) );
		} else if( this.phase.state == Phase.State.UNDEAD_DEFEATED && this.isLastWave() ) {
			Events.dispatch( new OnUndeadArmyDefeated( this ) );
		}
	}

	public double distanceTo( BlockPos position ) {
		return AnyPos.from( position.getCenter() ).dist2d( this.position.getCenter() ).doubleValue();
	}

	public boolean hasFinished() {
		return this.phase.state == Phase.State.FINISHED;
	}

	public boolean isInRange( BlockPos position ) {
		return this.distanceTo( position ) < UndeadArmyConfig.AREA_RADIUS;
	}

	public boolean isLastWave() {
		return this.currentWave == UndeadArmyConfig.WAVE_DEFS.stream().filter( waveDef->this.gameStage.getOrdinal() >= waveDef.gameStage.getOrdinal() ).count();
	}

	public boolean isPartOfWave( Entity entity ) {
		return this.mobsLeft.stream().anyMatch( mobInfo->mobInfo.uuid != null && mobInfo.uuid.equals( entity.getUUID() ) );
	}

	private void forEachSpawnedUndead( Consumer< LivingEntity > consumer ) {
		this.mobsLeft.stream()
			.map( mobInfo->mobInfo.toEntity( this.getLevel() ) )
			.filter( entity->entity != null )
			.forEach( entity->consumer.accept( ( LivingEntity )entity ) );
	}

	public ServerLevel getLevel() {
		return Side.getServer().overworld();
	}

	public enum Direction {
		WEST( -1, 0 ),
		EAST( 1, 0 ),
		NORTH( 0, -1 ),
		SOUTH( 0, 1 );

		public final int x, z;

		Direction( int x, int z ) {
			this.x = x;
			this.z = z;
		}
	}

	public static class Phase {
		public State state = State.CREATED;
		public int ticksLeft = 0;
		public int ticksTotal = 1;
		public int healthTotal = 0;

		public float getRatio() {
			return Mth.clamp( 1.0f - ( float )this.ticksLeft / this.ticksTotal, 0.0f, 1.0f );
		}

		public float getTicksActive() {
			return this.ticksTotal - this.ticksLeft;
		}

		public enum State {
			CREATED, STARTED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, FINISHED
		}
	}

	public static class MobInfo {
		public EntityType< ? > type;
		public ResourceLocation equipment;
		public BlockPos position;
		public boolean isBoss = false;
		public UUID uuid = null;

		public MobInfo( UndeadArmyConfig.MobDef def, BlockPos position, boolean isBoss ) {
			this.type = def.type.get();
			this.equipment = def.equipment;
			this.position = position;
			this.isBoss = isBoss;
		}

		public MobInfo() {}

		public @Nullable Entity toEntity( ServerLevel level ) {
			return this.uuid != null ? level.getEntity( this.uuid ) : null;
		}

		public float getHealth( ServerLevel level ) {
			return this.toEntity( level ) instanceof LivingEntity entity ? entity.getHealth() : 0.0f;
		}

		public float getMaxHealth( ServerLevel level ) {
			return this.toEntity( level ) instanceof LivingEntity entity ? entity.getMaxHealth() : 0.0f;
		}
	}
}

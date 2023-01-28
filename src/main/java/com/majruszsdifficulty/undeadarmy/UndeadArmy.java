package com.majruszsdifficulty.undeadarmy;

import com.mlib.data.SerializableStructure;
import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class UndeadArmy extends SerializableStructure {
	final ServerLevel level;
	final Config config;
	final List< IComponent > components = new ArrayList<>();
	final List< ServerPlayer > participants = new ArrayList<>();
	final List< MobInfo > mobsLeft = new ArrayList<>();
	BlockPos positionToAttack;
	Direction direction;
	Phase phase = new Phase();
	int currentWave = 0;

	public UndeadArmy( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;

		this.define( "mobs_left", ()->this.mobsLeft, this.mobsLeft::addAll, MobInfo::new );
		this.define( "position", ()->this.positionToAttack, x->this.positionToAttack = x );
		this.define( "direction", ()->this.direction, x->this.direction = x, Direction::values );
		this.define( "phase", ()->this.phase, x->this.phase = x, Phase::new );
		this.define( "current_wave", ()->this.currentWave, x->this.currentWave = x );
		this.addComponent( ParticipantsUpdater::new );
		this.addComponent( ProgressIndicator::new );
		this.addComponent( WaveController::new );
		this.addComponent( MessageSender::new );
		this.addComponent( MobSpawner::new );
	}

	public void highlightArmy() {

	}

	public void killAllUndeadArmyMobs() {

	}

	public int countMobsLeft() {
		return 0;
	}

	public void finish() {
		this.setState( Phase.State.FINISHED );
	}

	void tick() {
		this.components.forEach( IComponent::tick );
	}

	void setState( Phase.State state, int ticksLeft ) {
		this.phase.state = state;
		this.phase.ticksLeft = ticksLeft;
		this.phase.ticksTotal = Math.max( ticksLeft, 1 );

		this.components.forEach( IComponent::onPhaseChanged );
	}

	void setState( Phase.State state ) {
		this.setState( state, 0 );
	}

	double distanceTo( BlockPos position ) {
		return VectorHelper.distanceHorizontal( position.getCenter(), this.positionToAttack.getCenter() );
	}

	boolean hasFinished() {
		return this.phase.state == Phase.State.FINISHED;
	}

	boolean isInRange( BlockPos position ) {
		return this.distanceTo( position ) < 100.0f;
	}

	boolean isLastWave() {
		return this.currentWave == this.config.getWavesNum();
	}

	boolean isPhaseOver() {
		return this.getPhaseRatio() == 1.0f;
	}

	float getPhaseRatio() {
		return Mth.clamp( 1.0f - ( float )this.phase.ticksLeft / this.phase.ticksTotal, 0.0f, 1.0f );
	}

	private void addComponent( Function< UndeadArmy, IComponent > provider ) {
		this.components.add( provider.apply( this ) );
	}

	static class MobInfo extends SerializableStructure {
		EntityType< ? > type;
		ResourceLocation equipment;
		BlockPos position;
		boolean isBoss = false;
		UUID uuid = null;

		public MobInfo() {
			this.define( "type", ()->this.type, x->this.type = x );
			this.define( "equipment", ()->this.equipment, x->this.equipment = x );
			this.define( "position", ()->this.position, x->this.position = x );
			this.define( "is_boss", ()->this.isBoss, x->this.isBoss = x );
			this.define( "uuid", ()->this.uuid, x->this.uuid = x );
		}

		public MobInfo( Config.MobDef def, BlockPos position, boolean isBoss ) {
			this();

			this.type = def.type;
			this.equipment = def.equipment;
			this.position = position;
			this.isBoss = isBoss;
		}

		@Nullable
		public Entity toEntity( ServerLevel level ) {
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

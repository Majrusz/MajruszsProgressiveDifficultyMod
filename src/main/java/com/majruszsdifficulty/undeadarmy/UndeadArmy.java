package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.data.SerializableStructure;
import com.mlib.math.VectorHelper;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
	Entity boss = null;

	public UndeadArmy( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;

		this.define( "mobs_left", ()->this.mobsLeft, this.mobsLeft::addAll, MobInfo::new );
		this.define( "position", ()->this.positionToAttack, x->this.positionToAttack = x );
		this.define( "direction", ()->this.direction, x->this.direction = x, Direction::values );
		this.define( "phase", ()->this.phase, x->this.phase = x, Phase::new );
		this.define( "current_wave", ()->this.currentWave, x->this.currentWave = x );
		this.addComponent( ParticipantsUpdater::new );
		this.addComponent( BossUpdater::new );
		this.addComponent( MobSpawner::new );
		this.addComponent( WaveController::new );
		this.addComponent( WeatherController::new );
		this.addComponent( ProgressIndicator::new );
		this.addComponent( MessageSender::new );
		this.addComponent( ParticleSpawner::new );
		this.addComponent( SoundPlayer::new );
	}

	public void highlightArmy() {
		this.forEachSpawnedUndead( entity->MobEffectHelper.tryToApply( entity, MobEffects.GLOWING, Utility.secondsToTicks( 15.0 ), 0 ) );
	}

	public void killAllUndeadArmyMobs() {
		this.forEachSpawnedUndead( Entity::kill );
		this.mobsLeft.clear();
	}

	public void finish() {
		this.setState( Phase.State.FINISHED );
	}

	void start( BlockPos positionToAttack, Direction direction ) {
		this.positionToAttack = positionToAttack;
		this.direction = direction;
		this.setState( Phase.State.STARTED, Utility.secondsToTicks( 6.5 ) );

		this.components.forEach( IComponent::onStart );
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
		return this.distanceTo( position ) < this.config.getArmyRadius();
	}

	boolean isLastWave() {
		return this.currentWave == this.config.getWavesNum();
	}

	boolean isPartOfWave( Entity entity ) {
		return this.mobsLeft.stream().anyMatch( mobInfo->mobInfo.uuid != null && mobInfo.uuid.equals( entity.getUUID() ) );
	}

	@Override
	protected void onRead() {
		this.components.forEach( IComponent::onGameReload );
	}

	private void addComponent( Function< UndeadArmy, IComponent > provider ) {
		this.components.add( provider.apply( this ) );
	}

	private void forEachSpawnedUndead( Consumer< LivingEntity > consumer ) {
		this.mobsLeft.stream()
			.filter( mobInfo->mobInfo.uuid != null )
			.forEach( mobInfo->consumer.accept( ( LivingEntity )mobInfo.toEntity( this.level ) ) );
	}
}

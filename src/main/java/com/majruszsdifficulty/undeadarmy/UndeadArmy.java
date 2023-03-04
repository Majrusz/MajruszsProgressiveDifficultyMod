package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.undeadarmy.components.Components;
import com.majruszsdifficulty.undeadarmy.components.IComponent;
import com.majruszsdifficulty.undeadarmy.data.Direction;
import com.majruszsdifficulty.undeadarmy.data.MobInfo;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import com.mlib.Utility;
import com.mlib.data.SerializableStructure;
import com.mlib.entities.EntityHelper;
import com.mlib.math.VectorHelper;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UndeadArmy extends SerializableStructure {
	public final Components components = new Components( this );
	public final List< ServerPlayer > participants = new ArrayList<>();
	public final List< MobInfo > mobsLeft = new ArrayList<>();
	public final ServerLevel level;
	public final Config config;
	public BlockPos positionToAttack;
	public Direction direction;
	public Phase phase = new Phase();
	public int currentWave = 0;
	public Entity boss = null;
	public boolean areEntitiesLoaded = true;

	public UndeadArmy( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;

		this.define( "mobs_left", ()->this.mobsLeft, this.mobsLeft::addAll, MobInfo::new );
		this.define( "position", ()->this.positionToAttack, x->this.positionToAttack = x );
		this.define( "direction", ()->this.direction, x->this.direction = x, Direction::values );
		this.define( "phase", ()->this.phase, x->this.phase = x, Phase::new );
		this.define( "current_wave", ()->this.currentWave, x->this.currentWave = x );
	}

	public void start( BlockPos positionToAttack, Direction direction ) {
		this.positionToAttack = positionToAttack;
		this.direction = direction;
		this.setState( Phase.State.STARTED, Utility.secondsToTicks( 6.7 ) );

		this.components.dispatch( IComponent::onStart );
	}

	public void finish() {
		this.setState( Phase.State.FINISHED, 0 );
	}

	public void tick() {
		if( !this.areEntitiesLoaded ) {
			this.areEntitiesLoaded = this.mobsLeft.stream().allMatch( mobInfo->mobInfo.uuid == null || EntityHelper.isLoaded( this.level, mobInfo.uuid ) );
			if( this.areEntitiesLoaded ) {
				this.components.dispatch( IComponent::onGameReload );
			} else {
				return;
			}
		}
		if( this.level.getDifficulty() == Difficulty.PEACEFUL ) {
			this.finish();
			return;
		}

		this.components.dispatch( IComponent::tick );
	}

	public void highlightArmy() {
		this.forEachSpawnedUndead( entity->MobEffectHelper.tryToApply( entity, MobEffects.GLOWING, Utility.secondsToTicks( 15.0 ), 0 ) );
	}

	public void killAllUndeadArmyMobs() {
		this.forEachSpawnedUndead( Entity::kill );
		this.mobsLeft.clear();
	}

	public void setState( Phase.State state, int ticksLeft ) {
		this.phase.state = state;
		this.phase.ticksLeft = ticksLeft;
		this.phase.ticksTotal = Math.max( ticksLeft, 1 );

		this.components.dispatch( IComponent::onStateChanged );
		if( this.phase.state == Phase.State.WAVE_PREPARING && this.currentWave > 0 || this.phase.state == Phase.State.UNDEAD_DEFEATED && this.isLastWave() ) {
			this.components.dispatch( IComponent::onWaveFinished );
		}
	}

	public double distanceTo( BlockPos position ) {
		return VectorHelper.distanceHorizontal(
			new Vec3( position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5 ),
			new Vec3( this.positionToAttack.getX() + 0.5, this.positionToAttack.getY() + 0.5, this.positionToAttack.getZ() + 0.5 )
		);
	}

	public boolean hasFinished() {
		return this.phase.state == Phase.State.FINISHED;
	}

	public boolean isInRange( BlockPos position ) {
		return this.distanceTo( position ) < this.config.getArmyRadius();
	}

	public boolean isLastWave() {
		return this.currentWave == this.config.getWavesNum();
	}

	public boolean isPartOfWave( Entity entity ) {
		return this.mobsLeft.stream().anyMatch( mobInfo->mobInfo.uuid != null && mobInfo.uuid.equals( entity.getUUID() ) );
	}

	@Override
	protected void onRead() {
		this.areEntitiesLoaded = false;
	}

	private void forEachSpawnedUndead( Consumer< LivingEntity > consumer ) {
		this.mobsLeft.stream()
			.filter( mobInfo->mobInfo.uuid != null )
			.forEach( mobInfo->consumer.accept( ( LivingEntity )mobInfo.toEntity( this.level ) ) );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.items.WitherSwordItem;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.time.Time;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.phys.Vec3;

@AutoInstance
public class TurnSkeletonIntoWitherSkeleton extends GameModifier {
	static final String WITHER_TAG = "MajruszsDifficultyWitherTag";

	public TurnSkeletonIntoWitherSkeleton() {
		super( Registries.Modifiers.DEFAULT );

		OnDamaged.listen( this::applyWitherTag )
			.addCondition( Condition.predicate( data->data.attacker != null ) )
			.addCondition( Condition.predicate( data->data.attacker.getMainHandItem().getItem() instanceof WitherSwordItem ) )
			.addCondition( Condition.predicate( data->data.target instanceof Skeleton ) )
			.insertTo( this );

		OnDeath.listen( this::spawnWitherSkeleton )
			.addCondition( Condition.isServer() )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
			.addCondition( Condition.chanceCRD( 0.5, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( this::hasWitherTag ) )
			.insertTo( this );

		this.name( "TurnSkeletonIntoWitherSkeleton" )
			.comment( "If the Skeleton dies from Wither Sword it will respawn as Wither Skeleton in a few seconds." );
	}

	private void applyWitherTag( OnDamaged.Data data ) {
		data.target.getPersistentData().putBoolean( WITHER_TAG, true );
	}

	private void spawnWitherSkeleton( OnDeath.Data data ) {
		ServerLevel level = data.getServerLevel();
		Time.slider( 7.0, slider->{
			Vec3 position = data.target.position().add( 0.0, 1.0, 0.0 );
			if( slider.getTicksLeft() % 5 == 0 ) {
				ParticleHandler.SOUL.spawn( level, position, ( int )( slider.getRatio() * 10 ), ParticleHandler.offset( slider.getRatio() ) );
			}
			if( slider.getTicksLeft() == 2 ) {
				ParticleHandler.SOUL.spawn( level, position, 100, ParticleHandler.offset( 0.5f ) );
				ParticleHandler.SOUL.spawn( level, position, 100, ParticleHandler.offset( 1.0f ) );
			}
			if( slider.isFinished() ) {
				EntityType.WITHER_SKELETON.spawn( level, ( CompoundTag )null, null, new BlockPos( data.target.position() ), MobSpawnType.EVENT, true, true );
			}
		} );
	}

	private boolean hasWitherTag( OnDeath.Data data ) {
		return data.target.getPersistentData().getBoolean( WITHER_TAG );
	}
}

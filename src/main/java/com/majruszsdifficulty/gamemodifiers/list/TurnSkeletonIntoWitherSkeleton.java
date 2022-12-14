package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.items.WitherBowItem;
import com.majruszsdifficulty.items.WitherSwordItem;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnProjectileHit;
import com.mlib.time.Anim;
import com.mlib.time.Slider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;

@AutoInstance
public class TurnSkeletonIntoWitherSkeleton extends GameModifier {
	static final String WITHER_TAG = "MajruszsDifficultyWitherTag";

	public TurnSkeletonIntoWitherSkeleton() {
		super( Registries.Modifiers.DEFAULT, "TurnSkeletonIntoWitherSkeleton", "If the Skeleton dies from Wither Sword or Wither Bow it will respawn as Wither Skeleton in a few seconds." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this::applyWitherTag );
		onDamaged.addCondition( data->data.attacker != null )
			.addCondition( data->data.attacker.getMainHandItem().getItem() instanceof WitherSwordItem )
			.addCondition( data->data.target instanceof Skeleton );

		OnProjectileHit.Context onProjectileHit = new OnProjectileHit.Context( this::applyWitherTag );
		onProjectileHit.addCondition( data->data.weapon != null && data.weapon.getItem() instanceof WitherBowItem )
			.addCondition( data->data.entityHitResult != null && data.entityHitResult.getEntity() instanceof Skeleton );

		OnDeath.Context onDeath = new OnDeath.Context( this::spawnWitherSkeleton );
		onDeath.addCondition( new Condition.IsServer<>() )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.5, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( this::hasWitherTag );

		this.addContexts( onDamaged, onProjectileHit, onDeath );
	}

	private void applyWitherTag( OnDamaged.Data data ) {
		data.target.getPersistentData().putBoolean( WITHER_TAG, true );
	}

	private void applyWitherTag( OnProjectileHit.Data data ) {
		data.entityHitResult.getEntity().getPersistentData().putBoolean( WITHER_TAG, true );
	}

	private void spawnWitherSkeleton( OnDeath.Data data ) {
		Anim.slider( 7.0, slider->{
			if( slider.getTicksLeft() % 5 == 0 ) {
				ParticleHandler.SOUL.spawn( data.level, data.target.position()
					.add( 0.0, 1.0, 0.0 ), ( int )( slider.getRatio() * 10 ), ParticleHandler.offset( slider.getRatio() ) );
			}
			if( slider.getTicksLeft() == 2 ) {
				ParticleHandler.SOUL.spawn( data.level, data.target.position()
					.add( 0.0, 1.0, 0.0 ), 100, ParticleHandler.offset( 0.5f ) );
				ParticleHandler.SOUL.spawn( data.level, data.target.position()
					.add( 0.0, 1.0, 0.0 ), 100, ParticleHandler.offset( 1.0f ) );
			}
			if( slider.isFinished() ) {
				EntityType.WITHER_SKELETON.spawn( data.level, ( CompoundTag )null, null, new BlockPos( data.target.position() ), MobSpawnType.EVENT, true, true );
			}
		} );
	}

	private boolean hasWitherTag( OnDeath.Data data ) {
		return data.target.getPersistentData().getBoolean( WITHER_TAG );
	}
}

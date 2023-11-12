package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gui.BleedingGui;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityDamaged;
import com.mlib.contexts.OnEntityDied;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.entity.EffectHelper;
import com.mlib.entity.EntityHelper;
import com.mlib.math.Random;
import com.mlib.platform.Side;

@AutoInstance
public class BleedingParticles {
	public BleedingParticles() {
		OnEntityTicked.listen( this::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.15f ) )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING, data.entity ) );

		OnEntityDied.listen( this::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING, data.target ) );

		OnEntityDamaged.listen( this::addGuiOverlay )
			.addCondition( data->data.source.is( MajruszsDifficulty.BLEEDING_SOURCE ) );
	}

	private void emit( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.BLEEDING, data.entity ).orElse( 0 );
		float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( Random.round( 0.5 + 0.5 * ( 15.0 + amplifier ) * walkDistanceDelta ) )
			.sizeBased( data.entity )
			.emit( data.getServerLevel() );
	}

	private void emit( OnEntityDied data ) {
		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( 50 )
			.sizeBased( data.target )
			.emit( data.getServerLevel() );
	}

	private void addGuiOverlay( OnEntityDamaged data ) {
		Side.runOnClient( ()->()->BleedingGui.addBloodOnScreen( 3 ) );
	}
}

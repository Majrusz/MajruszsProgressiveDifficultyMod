package com.majruszsdifficulty.features;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gui.BleedingGui;
import com.majruszlibrary.contexts.OnEntityDied;
import com.majruszlibrary.contexts.OnEntityPreDamaged;
import com.majruszlibrary.contexts.OnEntityTicked;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.contexts.base.Priority;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;

public class BleedingParticles {
	static {
		OnEntityTicked.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.15f ) )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING, data.entity ) );

		OnEntityDied.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING, data.target ) );

		OnEntityPreDamaged.listen( BleedingParticles::addGuiOverlay )
			.priority( Priority.LOWEST )
			.addCondition( data->data.source.is( MajruszsDifficulty.BLEEDING_SOURCE ) );
	}

	private static void emit( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.BLEEDING, data.entity ).orElse( 0 );
		float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( Random.round( 0.5 + 0.5 * ( 15.0 + amplifier ) * walkDistanceDelta ) )
			.sizeBased( data.entity )
			.emit( data.getServerLevel() );
	}

	private static void emit( OnEntityDied data ) {
		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( 50 )
			.sizeBased( data.target )
			.emit( data.getServerLevel() );
	}

	private static void addGuiOverlay( OnEntityPreDamaged data ) {
		Side.runOnClient( ()->()->{
			if( data.target.equals( Side.getLocalPlayer() ) ) {
				BleedingGui.addBloodOnScreen( 3 );
			}
		} );
	}
}

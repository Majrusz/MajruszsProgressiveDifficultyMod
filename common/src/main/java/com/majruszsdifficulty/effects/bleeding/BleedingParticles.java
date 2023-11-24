package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.events.base.Priority;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;

public class BleedingParticles {
	static {
		OnEntityTicked.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.15f ) )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.Effects.BLEEDING, data.entity ) );

		OnEntityDied.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.Effects.BLEEDING, data.target ) );

		OnEntityPreDamaged.listen( BleedingParticles::addGuiOverlay )
			.priority( Priority.LOWEST )
			.addCondition( data->data.source.is( MajruszsDifficulty.DamageSources.BLEEDING ) );
	}

	private static void emit( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.Effects.BLEEDING, data.entity ).orElse( 0 );
		float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

		ParticleEmitter.of( MajruszsDifficulty.Particles.BLOOD )
			.count( Random.round( 0.5 + 0.5 * ( 15.0 + amplifier ) * walkDistanceDelta ) )
			.sizeBased( data.entity )
			.offset( AnyPos.from( data.entity.getBbWidth(), data.entity.getBbHeight(), data.entity.getBbWidth() ).mul( 0.25, 0.25, 0.25 ).vec3() )
			.emit( data.getServerLevel() );
	}

	private static void emit( OnEntityDied data ) {
		ParticleEmitter.of( MajruszsDifficulty.Particles.BLOOD )
			.count( 50 )
			.sizeBased( data.target )
			.offset( AnyPos.from( data.target.getBbWidth(), data.target.getBbHeight(), data.target.getBbWidth() ).mul( 0.25, 0.25, 0.25 ).vec3() )
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

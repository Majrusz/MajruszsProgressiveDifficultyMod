package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
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
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.server.level.ServerPlayer;

public class BleedingParticles {
	static {
		OnEntityTicked.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.15f ) )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING_EFFECT, data.entity ) );

		OnEntityDied.listen( BleedingParticles::emit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING_EFFECT, data.target ) );

		OnEntityPreDamaged.listen( BleedingParticles::addGuiOverlay )
			.priority( Priority.LOWEST )
			.addCondition( data->data.source.is( MajruszsDifficulty.BLEEDING_DAMAGE_SOURCE ) )
			.addCondition( data->data.target instanceof ServerPlayer );
	}

	private static void emit( OnEntityTicked data ) {
		int amplifier = EffectHelper.getAmplifier( MajruszsDifficulty.BLEEDING_EFFECT, data.entity ).orElse( 0 );
		float walkDistanceDelta = EntityHelper.getWalkDistanceDelta( data.entity );

		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( Random.round( 0.5 + 0.5 * ( 15.0 + amplifier ) * walkDistanceDelta ) )
			.sizeBased( data.entity )
			.offset( AnyPos.from( data.entity.getBbWidth(), data.entity.getBbHeight(), data.entity.getBbWidth() ).mul( 0.25, 0.25, 0.25 ).vec3() )
			.emit( data.getServerLevel() );
	}

	private static void emit( OnEntityDied data ) {
		ParticleEmitter.of( MajruszsDifficulty.BLOOD_PARTICLE )
			.count( 50 )
			.sizeBased( data.target )
			.offset( AnyPos.from( data.target.getBbWidth(), data.target.getBbHeight(), data.target.getBbWidth() ).mul( 0.25, 0.25, 0.25 ).vec3() )
			.emit( data.getServerLevel() );
	}

	private static void addGuiOverlay( OnEntityPreDamaged data ) {
		MajruszsDifficulty.BLEEDING_GUI.sendToClient( ( ServerPlayer )data.target, new Message( 3 ) );
	}

	public static class Message {
		int count;

		static {
			Serializables.get( Message.class )
				.define( "count", Reader.integer(), s->s.count, ( s, v )->s.count = v );
		}

		public Message( int count ) {
			this.count = count;
		}

		public Message() {}
	}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			MajruszsDifficulty.BLEEDING_GUI.addClientCallback( Client::onMessageReceived );
		}

		private static void onMessageReceived( Message message ) {
			BleedingGui.addBloodOnScreen( message.count );
		}
	}
}

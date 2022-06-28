package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import net.minecraftforge.event.world.ExplosionEvent;

public class PowerfulExplosions extends GameModifier {
	static final OnExplosionContext ON_EXPLOSION = new OnExplosionContext( PowerfulExplosions::modifyExplosion );
	static final DoubleConfig RADIUS_MULTIPLIER = new DoubleConfig( "radius_multiplier", "Multiplies explosion radius by the given value (this value is scaled by Clamped Regional Difficulty).", false, 1.2599, 1.0, 10.0 );
	static final DoubleConfig FIRE_CHANCE = new DoubleConfig( "fire_chance", "Gives all explosions a chance to cause fire (this value is scaled by Clamped Regional Difficulty).", false, 0.75, 0.0, 1.0 );

	static {
		ON_EXPLOSION.addCondition( new Condition.Excludable() );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.event instanceof ExplosionEvent.Start ) );
		ON_EXPLOSION.addConfigs( RADIUS_MULTIPLIER, FIRE_CHANCE );
	}

	public PowerfulExplosions() {
		super( GameModifier.DEFAULT, "PowerfulExplosions", "Makes all explosions (creepers, ghast ball etc.) much more deadly.", ON_EXPLOSION );
	}

	private static void modifyExplosion( com.mlib.gamemodifiers.GameModifier gameModifier, OnExplosionContext.Data data ) {
		double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( data.level, data.explosion.getPosition() );
		double radiusMultiplier = clampedRegionalDifficulty * ( RADIUS_MULTIPLIER.get() - 1.0 ) + 1.0;
		data.radius.setValue( radiusMultiplier * data.radius.getValue() );
		if( Random.tryChance( clampedRegionalDifficulty * FIRE_CHANCE.get() ) ) {
			data.causesFire.setValue( true );
		}
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import net.minecraftforge.event.world.ExplosionEvent;

public class PowerfulExplosions extends GameModifier {
	static final OnExplosionContext ON_EXPLOSION = new OnExplosionContext();

	static {
		ON_EXPLOSION.addCondition( new Condition.Excludable() );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.event instanceof ExplosionEvent.Start ) );
	}

	final DoubleConfig radiusMultiplier;
	final DoubleConfig fireChance;

	public PowerfulExplosions() {
		super( GameModifier.DEFAULT, "PowerfulExplosions", "Makes all explosions (creepers, ghast ball etc.) much more deadly.", ON_EXPLOSION );
		this.radiusMultiplier = new DoubleConfig( "radius_multiplier", "Multiplies explosion radius by the given value (this value is scaled by Clamped Regional Difficulty).", false, 1.2599, 1.0, 10.0 );
		this.fireChance = new DoubleConfig( "fire_chance", "Gives all explosions a chance to cause fire (this value is scaled by Clamped Regional Difficulty).", false, 0.75, 0.0, 1.0 );
		this.addConfigs( this.radiusMultiplier, this.fireChance );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnExplosionContext.Data explosionData ) {
			double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( explosionData.level, explosionData.explosion.getPosition() );
			double radiusMultiplier = clampedRegionalDifficulty * ( this.radiusMultiplier.get() - 1.0 ) + 1.0;
			explosionData.radius.setValue( radiusMultiplier * explosionData.radius.getValue() );
			if( Random.tryChance( clampedRegionalDifficulty * this.fireChance.get() ) ) {
				explosionData.causesFire.setValue( true );
			}
		}
	}
}

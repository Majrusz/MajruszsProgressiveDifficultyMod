package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosion;
import net.minecraftforge.event.level.ExplosionEvent;

@AutoInstance
public class PowerfulExplosions extends GameModifier {
	final DoubleConfig radiusMultiplier = new DoubleConfig( "radius_multiplier", "Multiplies explosion radius by the given value (this value is scaled by Clamped Regional Difficulty).", false, 1.2599, 1.0, 10.0 );
	final DoubleConfig fireChance = new DoubleConfig( "fire_chance", "Gives all explosions a chance to cause fire (this value is scaled by Clamped Regional Difficulty).", false, 0.75, 0.0, 1.0 );

	public PowerfulExplosions() {
		super( Registries.Modifiers.DEFAULT, "PowerfulExplosions", "Makes all explosions (creepers, ghast ball etc.) much more deadly." );

		OnExplosion.Context onExplosion = new OnExplosion.Context( this::modifyExplosion );
		onExplosion.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.level != null )
			.addCondition( data->data.event instanceof ExplosionEvent.Start )
			.addConfigs( this.radiusMultiplier, this.fireChance );

		this.addContext( onExplosion );
	}

	private void modifyExplosion( OnExplosion.Data data ) {
		double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( data.level, data.explosion.getPosition() );
		double radiusMultiplier = clampedRegionalDifficulty * ( this.radiusMultiplier.get() - 1.0 ) + 1.0;
		data.radius.setValue( radiusMultiplier * data.radius.getValue() );
		if( Random.tryChance( clampedRegionalDifficulty * this.fireChance.get() ) ) {
			data.causesFire.setValue( true );
		}
	}
}

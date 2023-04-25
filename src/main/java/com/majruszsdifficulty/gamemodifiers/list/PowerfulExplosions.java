package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnExplosionStart;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
import net.minecraft.core.BlockPos;

@AutoInstance
public class PowerfulExplosions {
	final DoubleConfig radiusMultiplier = new DoubleConfig( 1.2599, new Range<>( 1.0, 10.0 ) );
	final DoubleConfig fireChance = new DoubleConfig( 0.75, Range.CHANCE );

	public PowerfulExplosions() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "PowerfulExplosions" )
			.comment( "Makes all explosions (creepers, ghast ball etc.) much more deadly." );

		OnExplosionStart.listen( this::modifyExplosion )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addConfigs( this.radiusMultiplier
				.name( "radius_multiplier" )
				.comment( "Multiplies explosion radius by the given value (this value is scaled by Clamped Regional Difficulty)." )
			).addConfigs( this.fireChance
				.name( "fire_chance" )
				.comment( "Gives all explosions a chance to cause fire (this value is scaled by Clamped Regional Difficulty)." )
			).insertTo( group );
	}

	private void modifyExplosion( OnExplosionStart.Data data ) {
		BlockPos position = AnyPos.from( data.explosion.getPosition() ).block();
		double clampedRegionalDifficulty = LevelHelper.getClampedRegionalDifficultyAt( data.getServerLevel(), position );
		double radiusMultiplier = clampedRegionalDifficulty * ( this.radiusMultiplier.get() - 1.0 ) + 1.0;
		data.radius.setValue( radiusMultiplier * data.radius.getValue() );
		if( Random.tryChance( clampedRegionalDifficulty * this.fireChance.get() ) ) {
			data.causesFire.setValue( true );
		}
	}
}

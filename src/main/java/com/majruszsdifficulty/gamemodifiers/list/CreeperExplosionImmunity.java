package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.math.Range;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperExplosionImmunity extends GameModifier {
	final DoubleConfig damageMultiplier = new DoubleConfig( 0.2, new Range<>( 0.0, 0.99 ) );

	public CreeperExplosionImmunity() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::reduceExplosionDamage )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.source.isExplosion() )
			.addConfig( this.damageMultiplier.name( "damage_multiplier" ) )
			.insertTo( this );

		this.name( "CreeperExplosionImmunity" ).comment( "Decreases damage taken by Creepers from explosions." );
	}

	private void reduceExplosionDamage( OnDamaged.Data data ) {
		data.event.setAmount( ( float )( data.event.getAmount() * this.damageMultiplier.get() ) );
	}
}


package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity extends GameModifier {
	final DoubleConfig damageMultiplier = new DoubleConfig( "damage_multiplier", "", false, 0.2, 0.0, 0.99 );

	public CreeperExplosionImmunity() {
		super( GameModifier.DEFAULT, "CreeperExplosionImmunity", "Makes a Creeper take less damage from explosions." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::reduceExplosionDamage );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.ContextOnDamaged( data->data.target instanceof Creeper ) )
			.addCondition( new Condition.ContextOnDamaged( data->data.source.isExplosion() ) )
			.addConfig( this.damageMultiplier );

		this.addContext( onDamaged );
	}

	private void reduceExplosionDamage( OnDamagedData data ) {
		data.event.setAmount( ( float )( data.event.getAmount() * this.damageMultiplier.get() ) );
	}
}

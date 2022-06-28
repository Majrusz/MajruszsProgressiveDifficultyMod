package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( CreeperExplosionImmunity::reduceExplosionDamage );
	static final DoubleConfig DAMAGE_MULTIPLIER = new DoubleConfig( "damage_multiplier", "", false, 0.2, 0.0, 0.99 );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.target instanceof Creeper ) );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.isExplosion() ) );
		ON_DAMAGED.addConfig( DAMAGE_MULTIPLIER );
	}

	public CreeperExplosionImmunity() {
		super( GameModifier.DEFAULT, "CreeperExplosionImmunity", "Makes a Creeper take less damage from explosions.", ON_DAMAGED );
	}

	private static void reduceExplosionDamage( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		data.event.setAmount( ( float )( data.event.getAmount() * DAMAGE_MULTIPLIER.get() ) );
	}
}

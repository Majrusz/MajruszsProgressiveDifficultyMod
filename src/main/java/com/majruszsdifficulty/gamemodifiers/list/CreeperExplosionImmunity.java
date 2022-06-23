package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.config.DoubleConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.Context<>( OnDamagedContext.Data.class, data->data.target instanceof Creeper ) );
		ON_DAMAGED.addCondition( new Condition.Context<>( OnDamagedContext.Data.class, data->data.source.isExplosion() ) );
	}

	final DoubleConfig damageMultiplier;

	public CreeperExplosionImmunity() {
		super( GameModifier.DEFAULT, "CreeperExplosionImmunity", "Makes a Creeper take less damage from explosions.", ON_DAMAGED );
		this.damageMultiplier = new DoubleConfig( "damage_multiplier", "", false, 0.2, 0.0, 0.99 );
		this.configGroup.addConfig( this.damageMultiplier );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			damagedData.event.setAmount( ( float )( damagedData.event.getAmount() * this.damageMultiplier.get() ) );
		}
	}
}

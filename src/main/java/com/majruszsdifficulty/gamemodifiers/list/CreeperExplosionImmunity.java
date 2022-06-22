package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.config.DoubleConfig;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity extends GameModifier {
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.target instanceof Creeper ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.source.isExplosion() ) );
	}

	final DoubleConfig damageMultiplier;

	public CreeperExplosionImmunity() {
		super( "CreeperExplosionImmunity", "Makes a Creeper take less damage from explosions.", ON_DAMAGED );
		this.damageMultiplier = new DoubleConfig( "damage_multiplier", "", false, 0.2, 0.0, 0.99 );
		this.configGroup.addConfig( this.damageMultiplier );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			damagedData.event.setAmount( ( float )( damagedData.event.getAmount() * this.damageMultiplier.get() ) );
		}
	}
}

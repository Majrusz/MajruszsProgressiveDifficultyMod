package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.config.DoubleConfig;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity extends GameModifier {
	static final ICondition.Excludable EXCLUDABLE = new ICondition.Excludable();
	static final ICondition.GameStage GAME_STAGE = new ICondition.GameStage( GameStage.Stage.EXPERT );
	static final ICondition.Context< ? > IS_CREEPER_TARGET = new ICondition.Context<>( DamagedContext.Data.class, data->data.target instanceof Creeper );
	static final ICondition.Context< ? > IS_EXPLOSION_SOURCE = new ICondition.Context<>( DamagedContext.Data.class, data->data.source.isExplosion() );
	static final DamagedContext ON_DAMAGED = new DamagedContext( EXCLUDABLE, GAME_STAGE, IS_CREEPER_TARGET, IS_EXPLOSION_SOURCE );

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

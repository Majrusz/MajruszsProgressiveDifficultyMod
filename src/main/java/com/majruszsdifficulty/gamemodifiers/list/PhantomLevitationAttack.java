package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomLevitationAttack extends GameModifier {
	static final int MAX_TICKS = Utility.secondsToTicks( 60.0 );
	static final Config.Effect LEVITATION = new Config.Effect( "Levitation", 0, 5.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.75, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.attacker instanceof Phantom ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( LEVITATION );
	}

	public PhantomLevitationAttack() {
		super( "PhantomLevitationAttack", "Phantom attack may inflict stackable levitation effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.LEVITATION, LEVITATION.getDuration(), LEVITATION.getAmplifier(), MAX_TICKS );
		}
	}
}

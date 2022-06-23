package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

public class SpiderPoisonAttack extends GameModifier {
	static final Config.Effect POISON = new Config.Effect( "Poison", 0, new GameStage.Double( 4.0, 7.0, 15.0 ) );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.25, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.attacker instanceof Spider ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( POISON );
	}

	public SpiderPoisonAttack() {
		super( "SpiderPoisonAttack", "Spider attack may inflict poison effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.POISON, POISON.getDuration(), POISON.getAmplifier() );
		}
	}
}

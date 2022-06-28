package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

public class SpiderPoisonAttack extends GameModifier {
	static final ProgressiveEffectConfig POISON = new ProgressiveEffectConfig( "", ()->MobEffects.POISON, 0, new GameStage.Double( 4.0, 7.0, 15.0 ) );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( SpiderPoisonAttack::applyEffect );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new CustomConditions.CRDChance( 0.25 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof Spider ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( POISON );
	}

	public SpiderPoisonAttack() {
		super( GameModifier.DEFAULT, "SpiderPoisonAttack", "Spider attack may inflict poison effect.", ON_DAMAGED );
	}

	private static void applyEffect( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		POISON.apply( data.target );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

public class SpiderPoisonAttack extends GameModifier {
	final ProgressiveEffectConfig poison = new ProgressiveEffectConfig( "", ()->MobEffects.POISON, 0, new GameStage.Double( 4.0, 7.0, 15.0 ) );

	public SpiderPoisonAttack() {
		super( Registries.Modifiers.DEFAULT, "SpiderPoisonAttack", "Spider attack may inflict poison effect." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this::applyEffect );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.25, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.attacker instanceof Spider )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.poison );

		this.addContext( onDamaged );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.poison.apply( data.target );
	}
}

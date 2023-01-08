package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

@AutoInstance
public class SpiderPoisonAttack extends GameModifier {
	final ProgressiveEffectConfig poison = new ProgressiveEffectConfig( MobEffects.POISON, new GameStage.Integer( 0 ), new GameStage.Double( 4.0, 7.0, 15.0 ) );

	public SpiderPoisonAttack() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::applyEffect )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.25, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.attacker instanceof Spider )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.poison )
			.insertTo( this );

		this.name( "SpiderPoisonAttack" ).comment( "Spider attack may inflict poison effect." );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.poison.apply( data.target );
	}
}

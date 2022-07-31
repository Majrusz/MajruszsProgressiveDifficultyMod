package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

public class SpiderPoisonAttack extends DifficultyModifier {
	final ProgressiveEffectConfig poison = new ProgressiveEffectConfig( "", ()->MobEffects.POISON, 0, new GameStage.Double( 4.0, 7.0, 15.0 ) );

	public SpiderPoisonAttack() {
		super( DifficultyModifier.DEFAULT, "SpiderPoisonAttack", "Spider attack may inflict poison effect." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyEffect );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 0.25, true ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.attacker instanceof Spider )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.poison );

		this.addContext( onDamaged );
	}

	private void applyEffect( OnDamagedData data ) {
		this.poison.apply( data.target );
	}
}

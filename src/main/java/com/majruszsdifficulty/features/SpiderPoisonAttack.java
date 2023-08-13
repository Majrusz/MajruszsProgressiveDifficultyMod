package com.majruszsdifficulty.features;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.majruszsdifficulty.config.ProgressiveEffectConfig;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;

@AutoInstance
public class SpiderPoisonAttack {
	final ProgressiveEffectConfig poison = new ProgressiveEffectConfig( MobEffects.POISON, new GameStage.Integer( 0 ), new GameStage.Double( 4.0, 7.0, 15.0 ) );

	public SpiderPoisonAttack() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpiderPoisonAttack" )
			.comment( "Spider attack may inflict poison effect." );

		OnDamaged.listen( this::applyEffect )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.25, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Spider ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
			.addConfig( this.poison )
			.insertTo( group );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.poison.apply( data.target );
	}
}

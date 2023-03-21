package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Phantom;

@AutoInstance
public class PhantomLevitationAttack {
	final ProgressiveEffectConfig levitation = new ProgressiveEffectConfig( MobEffects.LEVITATION, 0, 5.0 ).stackable( 60.0 );

	public PhantomLevitationAttack() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "PhantomLevitationAttack" )
			.comment( "Phantom attack may inflict stackable levitation effect." );

		OnDamaged.listen( this::applyEffect )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
			.addCondition( Condition.chanceCRD( 0.75, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Phantom ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
			.addConfig( this.levitation )
			.insertTo( group );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.levitation.apply( data.target );
	}
}

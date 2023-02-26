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
import net.minecraft.world.entity.monster.Phantom;

@AutoInstance
public class PhantomLevitationAttack extends GameModifier {
	final ProgressiveEffectConfig levitation = new ProgressiveEffectConfig( MobEffects.LEVITATION, 0, 5.0 ).stackable( 60.0 );

	public PhantomLevitationAttack() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::applyEffect )
			.addCondition( new CustomConditions.GameStage<>( GameStage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.75, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.attacker instanceof Phantom )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.levitation )
			.insertTo( this );

		this.name( "PhantomLevitationAttack" ).comment( "Phantom attack may inflict stackable levitation effect." );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.levitation.apply( data.target );
	}
}

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
import net.minecraft.world.entity.monster.Shulker;

@AutoInstance
public class ShulkerBlindnessAttack extends GameModifier {
	final ProgressiveEffectConfig blindness = new ProgressiveEffectConfig( MobEffects.BLINDNESS, 0, 5.0 ).stackable( 60.0 );

	public ShulkerBlindnessAttack() {
		super( Registries.Modifiers.DEFAULT );

		OnDamaged.listen( this::applyEffect )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
			.addCondition( Condition.chanceCRD( 0.5, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Shulker ) )
			.addConfig( this.blindness )
			.insertTo( this );

		this.name( "ShulkerBlindnessAttack" ).comment( "Shulker attack may inflict stackable blindness effect." );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.blindness.apply( data.target );
	}
}

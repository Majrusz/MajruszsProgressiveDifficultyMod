package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerBlindnessAttack extends GameModifier {
	final ProgressiveEffectConfig blindness = new ProgressiveEffectConfig( "", ()->MobEffects.BLINDNESS, 0, 5.0, 60.0 );

	public ShulkerBlindnessAttack() {
		super( Registries.Modifiers.DEFAULT, "ShulkerBlindnessAttack", "Shulker attack may inflict stackable blindness effect." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyEffect );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance( 0.5, true ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.attacker instanceof Shulker )
			.addConfig( this.blindness );

		this.addContext( onDamaged );
	}

	private void applyEffect( OnDamagedData data ) {
		this.blindness.apply( data.target );
	}
}

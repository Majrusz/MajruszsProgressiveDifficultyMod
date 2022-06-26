package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerBlindnessAttack extends GameModifier {
	static final ProgressiveEffectConfig BLINDNESS = new ProgressiveEffectConfig( "", ()->MobEffects.BLINDNESS, 0, 5.0, 60.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new CustomConditions.CRDChance( 0.5 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof Shulker ) );
		ON_DAMAGED.addConfig( BLINDNESS );
	}

	public ShulkerBlindnessAttack() {
		super( GameModifier.DEFAULT, "ShulkerBlindnessAttack", "Shulker attack may inflict stackable blindness effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			BLINDNESS.apply( damagedData.target );
		}
	}
}

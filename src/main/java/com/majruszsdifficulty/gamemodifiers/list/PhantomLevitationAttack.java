package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomLevitationAttack extends GameModifier {
	static final CustomConfigs.ProgressiveEffect LEVITATION = new CustomConfigs.ProgressiveEffect( "Levitation", ()->MobEffects.LEVITATION, 0, 5.0, 60.0 );
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new CustomConditions.CRDChance( 0.75 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof Phantom ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( LEVITATION );
	}

	public PhantomLevitationAttack() {
		super( GameModifier.DEFAULT, "PhantomLevitationAttack", "Phantom attack may inflict stackable levitation effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			LEVITATION.apply( damagedData.target );
		}
	}
}

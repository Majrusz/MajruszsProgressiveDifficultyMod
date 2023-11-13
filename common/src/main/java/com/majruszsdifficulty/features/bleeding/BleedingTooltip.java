package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.majruszsdifficulty.data.EffectDef;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.annotation.AutoInstance;
import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.contexts.OnItemAttributeTooltip;
import com.mlib.contexts.base.Contexts;
import com.mlib.platform.Side;

@AutoInstance
@OnlyIn( Dist.CLIENT )
public class BleedingTooltip {
	public BleedingTooltip() {
		OnItemAttributeTooltip.listen( this::addCustomTooltip )
			.addCondition( BleedingEffect::isEnabled );
	}

	private void addCustomTooltip( OnItemAttributeTooltip data ) {
		EffectDef effectDef = BleedingEffect.getCurrentEffect( GameStageHelper.determineGameStage( Side.getLocalPlayer() ) );

		Contexts.dispatch( new OnBleedingTooltip( data, effectDef.amplifier ) );
	}
}

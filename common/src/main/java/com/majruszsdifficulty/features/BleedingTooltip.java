package com.majruszsdifficulty.features;

import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.majruszsdifficulty.data.EffectDef;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.contexts.OnItemAttributeTooltip;
import com.majruszlibrary.contexts.base.Contexts;
import com.majruszlibrary.platform.Side;

@OnlyIn( Dist.CLIENT )
public class BleedingTooltip {
	static {
		OnItemAttributeTooltip.listen( BleedingTooltip::addCustom )
			.addCondition( BleedingEffect::isEnabled );
	}

	private static void addCustom( OnItemAttributeTooltip data ) {
		EffectDef effectDef = BleedingEffect.getCurrentEffect( GameStageHelper.determineGameStage( Side.getLocalPlayer() ) );

		Contexts.dispatch( new OnBleedingTooltip( data, effectDef.amplifier ) );
	}
}

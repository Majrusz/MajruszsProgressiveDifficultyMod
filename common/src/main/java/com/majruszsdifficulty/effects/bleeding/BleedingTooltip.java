package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.entity.EffectDef;
import com.majruszlibrary.events.OnItemAttributeTooltip;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import com.majruszsdifficulty.gamestage.GameStageHelper;

@OnlyIn( Dist.CLIENT )
public class BleedingTooltip {
	static {
		OnItemAttributeTooltip.listen( BleedingTooltip::addCustom )
			.addCondition( Bleeding::isEnabled )
			.addCondition( data->Side.getLocalPlayer() != null /* compatibility check */ );
	}

	private static void addCustom( OnItemAttributeTooltip data ) {
		EffectDef effectDef = Bleeding.getCurrentEffect( GameStageHelper.determineGameStage( Side.getLocalPlayer() ) );

		Events.dispatch( new OnBleedingTooltip( data, effectDef.amplifier ) );
	}
}

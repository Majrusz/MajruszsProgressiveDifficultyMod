package com.majruszsdifficulty.events.base;

import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.events.type.ILevelEvent;
import com.majruszlibrary.events.type.IPositionEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;

public class CustomCondition {
	public static < DataType extends ILevelEvent & IPositionEvent > Condition< DataType > isEnabled( GameStageValue< Boolean > isEnabled ) {
		return Condition.predicate( data->isEnabled.get( GameStageHelper.determineGameStage( data ) ) );
	}
}

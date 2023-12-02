package com.majruszsdifficulty.events.base;

import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.events.type.ILevelEvent;
import com.majruszlibrary.events.type.IPositionEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;

public class CustomCondition {
	public static < DataType extends ILevelEvent & IPositionEvent > Condition< DataType > check( GameStage gameStage ) {
		return Condition.predicate( data->GameStageHelper.determineGameStage( data ).getOrdinal() >= gameStage.getOrdinal() );
	}
}

package com.majruszsdifficulty.features.item_sets;

import net.minecraft.world.entity.player.Player;

public class BonusData {
	public final int requiredItems;
	public final String translationKey;
	public final ICondition condition;

	public BonusData( int requiredItems, String translationKey, ICondition condition ) {
		this.requiredItems = requiredItems;
		this.translationKey = translationKey;
		this.condition = condition.and( ( set, player ) -> set.countSetItems( player ) >= requiredItems );
	}

	public BonusData( int requiredItems, String translationKey ) {
		this.requiredItems = requiredItems;
		this.translationKey = translationKey;
		this.condition = ( set, player ) -> set.countSetItems( player ) >= requiredItems;
	}

	public interface ICondition {
		boolean validate( BaseSet set, Player player );

		default ICondition and( ICondition otherCondition ) {
			return ( set, player )->this.validate( set, player ) && otherCondition.validate( set, player );
		}

		default ICondition or( ICondition otherCondition ) {
			return ( set, player )->this.validate( set, player ) || otherCondition.validate( set, player );
		}
	}

}
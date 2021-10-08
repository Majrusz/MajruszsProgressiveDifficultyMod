package com.majruszs_difficulty.features.item_sets;

public class BonusData {
	public final int requiredItems;
	public final String translationKey;
	public final IExtraValidator extraValidator;

	public BonusData( int requiredItems, String translationKey, IExtraValidator extraValidator ) {
		this.requiredItems = requiredItems;
		this.translationKey = translationKey;
		this.extraValidator = extraValidator;
	}

	public BonusData( int requiredItems, String translationKey ) {
		this( requiredItems, translationKey, player->true );
	}
}
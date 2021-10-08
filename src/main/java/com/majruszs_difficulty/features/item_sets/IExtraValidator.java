package com.majruszs_difficulty.features.item_sets;

import net.minecraft.world.entity.player.Player;

public interface IExtraValidator {
	boolean validate( Player player );

	default IExtraValidator and( IExtraValidator extraValidator ) {
		return player->this.validate( player ) && extraValidator.validate( player );
	}

	default IExtraValidator or( IExtraValidator extraValidator ) {
		return player->this.validate( player ) || extraValidator.validate( player );
	}
}

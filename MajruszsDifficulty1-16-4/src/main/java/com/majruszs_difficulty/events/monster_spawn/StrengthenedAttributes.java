package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.server.ServerWorld;

/** Increases damage and health of spawning hostile entities. */
public class StrengthenedAttributes {
	protected static final AttributeHelper maxHealthAttribute = new AttributeHelper( "ba9de909-4a9e-43da-9d14-fbcbc2403316",
		"MonsterSpawnHealthBonus", AttributeHelper.Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE
	);
	protected static final AttributeHelper damageAttribute = new AttributeHelper( "053d92c8-ccb5-4b95-9add-c31aca144177", "MonsterSpawnDamageBonus",
		AttributeHelper.Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE
	);

	public static void strengthenLivingEntity( LivingEntity livingEntity, ServerWorld world ) {
		double bonusMultiplier = getAttributeMultiplier( world );

		maxHealthAttribute.setValue( getHealthBonusMultiplier() * bonusMultiplier )
			.apply( livingEntity );
		damageAttribute.setValue( getDamageBonusMultiplier() * bonusMultiplier )
			.apply( livingEntity );

		livingEntity.setHealth( livingEntity.getMaxHealth() );
	}

	/**
	 Returns current attribute multiplier.

	 @param world World the multiplier is depending on.
	 */
	protected static double getAttributeMultiplier( ServerWorld world ) {
		double multiplier = 1.0;

		if( world.isNightTime() )
			multiplier *= Config.getDouble( Config.Values.DAMAGE_AND_HEALTH_MULTIPLIER_AT_NIGHT );

		return multiplier;
	}

	/** Returns health bonus depending on current game state. */
	private static double getHealthBonusMultiplier() {
		return GameState.getDoubleDependingOnGameState( Config.Values.HEALTH_BONUS_NORMAL, Config.Values.HEALTH_BONUS_EXPERT,
			Config.Values.HEALTH_BONUS_MASTER
		);
	}

	/** Returns damage bonus depending on current game state. */
	private static double getDamageBonusMultiplier() {
		return GameState.getDoubleDependingOnGameState( Config.Values.DAMAGE_BONUS_NORMAL, Config.Values.DAMAGE_BONUS_EXPERT,
			Config.Values.DAMAGE_BONUS_MASTER
		);
	}
}

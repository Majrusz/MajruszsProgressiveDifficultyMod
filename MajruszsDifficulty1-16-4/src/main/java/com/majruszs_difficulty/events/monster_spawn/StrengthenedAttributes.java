package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.server.ServerWorld;

public class StrengthenedAttributes {
	protected static class BonusMultipliers {
		public static final double health = 0.25D, damage = 0.25D;
	}

	protected static final AttributeHelper maxHealthAttribute = new AttributeHelper( "ba9de909-4a9e-43da-9d14-fbcbc2403316",
		"MonsterSpawnHealthBonus", AttributeHelper.Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE
	);
	protected static final AttributeHelper damageAttribute = new AttributeHelper( "053d92c8-ccb5-4b95-9add-c31aca144177", "MonsterSpawnDamageBonus",
		AttributeHelper.Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE
	);

	public static void strengthenLivingEntity( LivingEntity livingEntity, ServerWorld world ) {
		if( !GameState.atLeast( GameState.State.EXPERT ) )
			return;

		double bonusMultiplier = getAttributeMultiplier( world );

		maxHealthAttribute.setValue( BonusMultipliers.health * bonusMultiplier )
			.apply( livingEntity );
		damageAttribute.setValue( BonusMultipliers.damage * bonusMultiplier )
			.apply( livingEntity );

		livingEntity.setHealth( livingEntity.getMaxHealth() );
	}

	protected static double getAttributeMultiplier( ServerWorld world ) {
		double multiplier = 1.0D;

		if( GameState.atLeast( GameState.State.MASTER ) )
			multiplier *= 2.0D;

		if( world.isNightTime() )
			multiplier *= 2.0D;

		return multiplier;
	}
}

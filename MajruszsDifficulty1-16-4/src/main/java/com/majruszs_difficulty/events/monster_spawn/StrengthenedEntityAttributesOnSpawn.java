package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.server.ServerWorld;

/** Increases damage and health of spawning hostile entities. */
public class StrengthenedEntityAttributesOnSpawn extends OnEnemyToBeSpawnedBase {
	protected static final AttributeHelper MAX_HEALTH_ATTRIBUTE = new AttributeHelper( "ba9de909-4a9e-43da-9d14-fbcbc2403316",
		"MonsterSpawnHealthBonus", AttributeHelper.Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE
	);
	protected static final AttributeHelper DAMAGE_ATTRIBUTE = new AttributeHelper( "053d92c8-ccb5-4b95-9add-c31aca144177", "MonsterSpawnDamageBonus",
		AttributeHelper.Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE
	);
	private static final String CONFIG_NAME = "SpawnBonuses";
	private static final String CONFIG_COMMENT = "Damage and health bonuses of hostile creatures.";
	protected final GameStateDoubleConfig healthBonuses;
	protected final GameStateDoubleConfig damageBonuses;
	protected final DoubleConfig multiplierAtNight;

	public StrengthenedEntityAttributesOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.NORMAL, false );

		String health_comment = "Extra health multiplier.";
		String damage_comment = "Extra damage multiplier.";
		String night_comment = "Extra damage and health multiplier at night.";
		this.healthBonuses = new GameStateDoubleConfig( "health_bonus", health_comment, 0.0, 0.2, 0.4, 0.0, 10.0 );
		this.damageBonuses = new GameStateDoubleConfig( "damage_bonus", damage_comment, 0.0, 0.2, 0.4, 0.0, 10.0 );
		this.multiplierAtNight = new DoubleConfig( "night_multiplier", night_comment, false, 2.0, 1.0, 10.0 );
		this.featureGroup.addConfigs( this.healthBonuses, this.damageBonuses, this.multiplierAtNight );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		double bonusMultiplier = getAttributeMultiplier( world );

		MAX_HEALTH_ATTRIBUTE.setValue( this.healthBonuses.getCurrentGameStateValue() * bonusMultiplier )
			.apply( entity );
		DAMAGE_ATTRIBUTE.setValue( this.damageBonuses.getCurrentGameStateValue() * bonusMultiplier )
			.apply( entity );

		entity.setHealth( entity.getMaxHealth() );
	}

	/**
	 Returns current attribute multiplier.

	 @param world World the multiplier is depending on.
	 */
	protected double getAttributeMultiplier( ServerWorld world ) {
		double multiplier = 1.0;

		if( world.isNightTime() )
			multiplier *= this.multiplierAtNight.get();

		return multiplier;
	}
}

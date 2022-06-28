package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.attributes.AttributeHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobsSpawnStronger extends GameModifier {
	static final AttributeHandler MAX_HEALTH_ATTRIBUTE = new AttributeHandler( "ba9de909-4a9e-43da-9d14-fbcbc2403316", "ProgressiveDifficultyHealthBonus", Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE );
	static final AttributeHandler DAMAGE_ATTRIBUTE = new AttributeHandler( "053d92c8-ccb5-4b95-9add-c31aca144177", "ProgressiveDifficultyDamageBonus", Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( MobsSpawnStronger::makeMobsStronger );
	static final GameStageDoubleConfig HEALTH_BONUS = new GameStageDoubleConfig( "HealthBonusMultiplier", "", 0.0, 0.15, 0.3, 0.0, 10.0 );
	static final GameStageDoubleConfig DAMAGE_BONUS = new GameStageDoubleConfig( "DamageBonusMultiplier", "", 0.0, 0.15, 0.3, 0.0, 10.0 );
	static final GameStageDoubleConfig NIGHT_MULTIPLIER = new GameStageDoubleConfig( "NightMultiplier", "Multiplies health and damage bonuses at night.", 2.0, 2.0, 2.0, 1.0, 10.0 );

	static {
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Mob && AttributeHandler.hasAttribute( data.target, Attributes.ATTACK_DAMAGE ) && data.level != null ) );
		ON_SPAWNED.addConfigs( HEALTH_BONUS, DAMAGE_BONUS, NIGHT_MULTIPLIER );
	}

	public MobsSpawnStronger() {
		super( GameModifier.DEFAULT, "MobsSpawnStronger", "All hostile mobs get damage and health bonuses.", ON_SPAWNED );
	}

	private static void makeMobsStronger( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		assert data.level != null;
		LivingEntity entity = data.target;
		double nightMultiplier = data.level.isNight() ? NIGHT_MULTIPLIER.get() : 1.0;

		MAX_HEALTH_ATTRIBUTE.setValue( HEALTH_BONUS.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		DAMAGE_ATTRIBUTE.setValue( DAMAGE_BONUS.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		entity.setHealth( entity.getMaxHealth() );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.attributes.AttributeHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobsSpawnStronger extends GameModifier {
	static final AttributeHandler MAX_HEALTH_ATTRIBUTE = new AttributeHandler( "ba9de909-4a9e-43da-9d14-fbcbc2403316", "ProgressiveDifficultyHealthBonus", Attributes.MAX_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE );
	static final AttributeHandler DAMAGE_ATTRIBUTE = new AttributeHandler( "053d92c8-ccb5-4b95-9add-c31aca144177", "ProgressiveDifficultyDamageBonus", Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_BASE );
	final GameStageDoubleConfig healthBonus = new GameStageDoubleConfig( "HealthBonusMultiplier", "", 0.0, 0.15, 0.3, 0.0, 10.0 );
	final GameStageDoubleConfig damageBonus = new GameStageDoubleConfig( "DamageBonusMultiplier", "", 0.0, 0.15, 0.3, 0.0, 10.0 );
	final GameStageDoubleConfig nightMultiplier = new GameStageDoubleConfig( "NightMultiplier", "Multiplies health and damage bonuses at night.", 2.0, 2.0, 2.0, 1.0, 10.0 );

	public MobsSpawnStronger() {
		super( GameModifier.DEFAULT, "MobsSpawnStronger", "All hostile mobs get damage and health bonuses." );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::makeMobsStronger );
		onSpawned.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Mob && AttributeHandler.hasAttribute( data.target, Attributes.ATTACK_DAMAGE ) && data.level != null ) )
			.addConfigs( this.healthBonus, this.damageBonus, this.nightMultiplier );

		this.addContext( onSpawned );
	}

	private void makeMobsStronger( OnSpawnedData data ) {
		assert data.level != null;
		LivingEntity entity = data.target;
		double nightMultiplier = data.level.isNight() ? this.nightMultiplier.get() : 1.0;

		MAX_HEALTH_ATTRIBUTE.setValue( this.healthBonus.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		DAMAGE_ATTRIBUTE.setValue( this.damageBonus.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		entity.setHealth( entity.getMaxHealth() );
	}
}

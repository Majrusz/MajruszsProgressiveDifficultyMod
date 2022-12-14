package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.StringListConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.server.level.ServerLevel;
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
	final StringListConfig excludedMobs = new StringListConfig( "excluded_mobs", "List of mobs that should not get health and damage bonuses. (for instance minecraft:wither)", false );
	final StringListConfig excludedDimensions = new StringListConfig( "excluded_dimensions", "List of dimensions where health and damage bonuses should not be applied. (for instance minecraft:overworld)", false );

	public MobsSpawnStronger() {
		super( Registries.Modifiers.DEFAULT, "MobsSpawnStronger", "All hostile mobs get damage and health bonuses." );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::makeMobsStronger );
		onSpawned.addCondition( new Condition.Excludable<>() )
			.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
			.addCondition( data->data.level != null )
			.addCondition( data->this.canMobAttack( data.target ) )
			.addCondition( data->this.isNotDimensionExcluded( data.level ) )
			.addCondition( data->this.isNotMobExcluded( data.target ) )
			.addConfigs( this.healthBonus, this.damageBonus, this.nightMultiplier, this.excludedMobs, this.excludedDimensions );

		this.addContext( onSpawned );
	}

	private void makeMobsStronger( OnSpawned.Data data ) {
		assert data.level != null;
		LivingEntity entity = data.target;
		double nightMultiplier = data.level.isNight() ? this.nightMultiplier.get() : 1.0;

		MAX_HEALTH_ATTRIBUTE.setValue( this.healthBonus.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		DAMAGE_ATTRIBUTE.setValue( this.damageBonus.getCurrentGameStageValue() * nightMultiplier ).apply( entity );
		entity.setHealth( entity.getMaxHealth() );
	}

	private boolean canMobAttack( LivingEntity entity ) {
		return entity instanceof Mob && AttributeHandler.hasAttribute( entity, Attributes.ATTACK_DAMAGE );
	}

	private boolean isNotDimensionExcluded( ServerLevel level ) {
		return !this.excludedDimensions.contains( Utility.getRegistryString( level ) );
	}

	private boolean isNotMobExcluded( LivingEntity entity ) {
		return !this.excludedMobs.contains( Utility.getRegistryString( entity ) );
	}
}

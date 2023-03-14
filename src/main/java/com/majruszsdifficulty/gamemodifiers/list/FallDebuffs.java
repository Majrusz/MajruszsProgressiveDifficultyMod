package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

@AutoInstance
public class FallDebuffs {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( MobEffects.CONFUSION, new GameStage.Integer( 0 ), new GameStage.Double( 8.0 ) );
	final ProgressiveEffectConfig slowness = new ProgressiveEffectConfig( MobEffects.MOVEMENT_SLOWDOWN, new GameStage.Integer( 0 ), new GameStage.Double( 6.0 ) );

	public FallDebuffs() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "FallDebuffs" )
			.comment( "Inflicts several debuffs when taking fall damage." );

		OnDamaged.listen( this::applyDebuffs )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 1.0, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f ) )
			.addCondition( Condition.predicate( data->EntityHelper.isHuman( data.target ) ) )
			.addConfig( this.nausea.name( "Nausea" ) )
			.addConfig( this.slowness.name( "Slowness" ) )
			.insertTo( group );
	}

	private void applyDebuffs( OnDamaged.Data data ) {
		this.nausea.apply( data.target );
		this.slowness.apply( data.target );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

@AutoInstance
public class FallDebuffs extends GameModifier {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( MobEffects.CONFUSION, new GameStage.Integer( 0 ), new GameStage.Double( 8.0 ) );
	final ProgressiveEffectConfig slowness = new ProgressiveEffectConfig( MobEffects.MOVEMENT_SLOWDOWN, new GameStage.Integer( 0 ), new GameStage.Double( 6.0 ) );

	public FallDebuffs() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::applyDebuffs )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 1.0, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.source.equals( DamageSource.FALL ) && data.event.getAmount() > 2.0f )
			.addCondition( data->EntityHelper.isHuman( data.target ) )
			.addConfig( this.nausea.name( "Nausea" ) )
			.addConfig( this.slowness.name( "Slowness" ) )
			.insertTo( this );

		this.name( "FallDebuffs" ).comment( "Inflicts several debuffs when taking fall damage." );
	}

	private void applyDebuffs( OnDamaged.Data data ) {
		this.nausea.apply( data.target );
		this.slowness.apply( data.target );
	}
}

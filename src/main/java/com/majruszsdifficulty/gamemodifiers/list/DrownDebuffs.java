package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;

@AutoInstance
public class DrownDebuffs {
	final ProgressiveEffectConfig nausea = new ProgressiveEffectConfig( MobEffects.CONFUSION, new GameStage.Integer( 0 ), new GameStage.Double( 2.0 ) ).stackable( 60.0 );
	final ProgressiveEffectConfig weakness = new ProgressiveEffectConfig( MobEffects.WEAKNESS, new GameStage.Integer( 0 ), new GameStage.Double( 10.0 ) ).stackable( 60.0 );

	public DrownDebuffs() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "DrownDebuffs" )
			.comment( "Inflicts several debuffs when taking drown damage (these debuffs stack)." );

		OnDamaged.listen( this::applyDebuffs )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 1.0, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.source.is( DamageTypeTags.IS_DROWNING ) ) )
			.addConfig( this.nausea.name( "Nausea" ) )
			.addConfig( this.weakness.name( "Weakness" ) )
			.insertTo( group );
	}

	private void applyDebuffs( OnDamaged.Data data ) {
		this.nausea.apply( data.target );
		this.weakness.apply( data.target );
	}
}

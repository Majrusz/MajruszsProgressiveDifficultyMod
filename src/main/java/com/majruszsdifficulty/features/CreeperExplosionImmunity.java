package com.majruszsdifficulty.features;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import com.mlib.math.Range;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperExplosionImmunity {
	final DoubleConfig damageMultiplier = new DoubleConfig( 0.2, new Range<>( 0.0, 0.99 ) );

	public CreeperExplosionImmunity() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperExplosionImmunity" )
			.comment( "Decreases damage taken by Creepers from explosions." );

		OnDamaged.listen( this::reduceExplosionDamage )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper ) )
			.addCondition( Condition.predicate( data->data.source.isExplosion() ) )
			.addConfig( this.damageMultiplier.name( "damage_multiplier" ) )
			.insertTo( group );
	}

	private void reduceExplosionDamage( OnDamaged.Data data ) {
		data.event.setAmount( ( float )( data.event.getAmount() * this.damageMultiplier.get() ) );
	}
}

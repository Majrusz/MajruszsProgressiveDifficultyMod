package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.math.Range;
import net.minecraft.tags.DamageTypeTags;
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
			.addCondition( Condition.predicate( data->data.source.is( DamageTypeTags.IS_EXPLOSION ) ) )
			.addConfig( this.damageMultiplier.name( "damage_multiplier" ) )
			.insertTo( group );
	}

	private void reduceExplosionDamage( OnDamaged.Data data ) {
		data.event.setAmount( ( float )( data.event.getAmount() * this.damageMultiplier.get() ) );
	}
}

package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Shulker;

@AutoInstance
public class ShulkerBlindnessAttack {
	final ProgressiveEffectConfig blindness = new ProgressiveEffectConfig( MobEffects.BLINDNESS, 0, 5.0 ).stackable( 60.0 );

	public ShulkerBlindnessAttack() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "ShulkerBlindnessAttack" )
			.comment( "Shulker attack may inflict stackable blindness effect." );

		OnDamaged.listen( this::applyEffect )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
			.addCondition( Condition.chanceCRD( 0.5, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Shulker ) )
			.addConfig( this.blindness )
			.insertTo( group );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.blindness.apply( data.target );
	}
}

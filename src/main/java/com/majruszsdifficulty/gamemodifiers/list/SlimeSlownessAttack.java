package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.ProgressiveEffectConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Slime;

@AutoInstance
public class SlimeSlownessAttack extends GameModifier {
	final ProgressiveEffectConfig slowness = new ProgressiveEffectConfig( MobEffects.MOVEMENT_SLOWDOWN, 0, 6.0 );

	public SlimeSlownessAttack() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::applyEffect )
			.addCondition( new CustomConditions.GameStage<>( GameStage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.5, true ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.attacker instanceof Slime )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.slowness )
			.insertTo( this );

		this.name( "SlimeSlownessAttack" ).comment( "Shulker attack may inflict stackable blindness effect." );
	}

	private void applyEffect( OnDamaged.Data data ) {
		this.slowness.apply( data.target );
	}
}

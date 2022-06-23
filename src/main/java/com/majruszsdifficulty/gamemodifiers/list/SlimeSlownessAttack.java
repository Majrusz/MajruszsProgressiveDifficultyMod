package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Slime;

public class SlimeSlownessAttack extends GameModifier {
	static final Config.Effect SLOWNESS = new Config.Effect( "Slowness", 0, 6.0 );
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.5, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.attacker instanceof Slime ) );
		ON_DAMAGED.addCondition( new DamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( SLOWNESS );
	}

	public SlimeSlownessAttack() {
		super( "ShulkerBlindnessAttack", "Shulker attack may inflict stackable blindness effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS.getDuration(), SLOWNESS.getAmplifier() );
		}
	}
}

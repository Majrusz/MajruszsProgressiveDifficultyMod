package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerBlindnessAttack extends GameModifier {
	static final int MAX_TICKS = Utility.secondsToTicks( 60.0 );
	static final Config.Effect BLINDNESS = new Config.Effect( "Blindness", 0, 5.0 );
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.5, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.attacker instanceof Shulker ) );
		ON_DAMAGED.addConfig( BLINDNESS );
	}

	public ShulkerBlindnessAttack() {
		super( "ShulkerBlindnessAttack", "Shulker attack may inflict stackable blindness effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			EffectHelper.stackEffectIfPossible( damagedData.target, MobEffects.BLINDNESS, BLINDNESS.getDuration(), BLINDNESS.getAmplifier(), MAX_TICKS );
		}
	}
}

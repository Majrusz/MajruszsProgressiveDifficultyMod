package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.majruszsdifficulty.items.WitherSwordItem;
import com.mlib.effects.EffectHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class WitherSwordEffect extends GameModifier {
	public static final Config.Effect WITHER = new Config.Effect( "Wither", 1, 6.0 );
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 1.0, false ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->ItemHelper.hasInMainHand( data.attacker, WitherSwordItem.class ) ) );
		ON_DAMAGED.addCondition( new DamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( WITHER );
	}

	public WitherSwordEffect() {
		super( "WitherSwordEffect", "Wither sword inflicts wither effect.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			EffectHelper.applyEffectIfPossible( damagedData.target, MobEffects.WITHER, WITHER.getDuration(), WITHER.getAmplifier() );
		}
	}
}

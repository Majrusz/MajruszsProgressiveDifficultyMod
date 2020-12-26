package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ApplyingNegativePotionEffectsOnCreeper {
	protected static final double effectChance = 0.375D;
	protected static final int effectDurationInTicks = MajruszsHelper.minutesToTicks( 5.0D );
	protected static final Effect[] effects = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.MINING_FATIGUE, Effects.SATURATION };

	public static void tryToApply( CreeperEntity creeper ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= effectChance )
			return;

		Effect randomEffect = effects[ MajruszsDifficulty.RANDOM.nextInt( effects.length ) ];
		creeper.addPotionEffect( new EffectInstance( randomEffect, effectDurationInTicks, 0 ) );
	}
}

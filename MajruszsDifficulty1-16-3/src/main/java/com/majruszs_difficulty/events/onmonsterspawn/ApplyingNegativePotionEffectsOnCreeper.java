package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ApplyingNegativePotionEffectsOnCreeper {
	protected static final double effectChance = 0.375D;
	protected static final int effectDurationInTicks = MajruszsHelper.minutesToTicks( 5.0D );

	protected static final EffectInstance[] effects = new EffectInstance[]{
		new EffectInstance( Effects.WEAKNESS, effectDurationInTicks, 0 ),
		new EffectInstance( Effects.SLOWNESS, effectDurationInTicks, 0 ),
		new EffectInstance( Effects.MINING_FATIGUE, effectDurationInTicks, 0 ),
		new EffectInstance( Effects.SATURATION, effectDurationInTicks, 0 )
	};

	public static void tryToApply( CreeperEntity creeper ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= effectChance )
			return;

		creeper.addPotionEffect( effects[ MajruszsDifficulty.RANDOM.nextInt( effects.length ) ] );
	}
}

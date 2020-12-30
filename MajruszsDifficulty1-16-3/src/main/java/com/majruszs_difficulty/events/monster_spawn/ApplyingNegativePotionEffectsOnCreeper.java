package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.server.ServerWorld;

public class ApplyingNegativePotionEffectsOnCreeper {
	protected static final int effectDurationInTicks = MajruszsHelper.minutesToTicks( 5.0D );
	protected static final Effect[] effects = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.MINING_FATIGUE, Effects.SATURATION };

	public static void tryToApply( CreeperEntity creeper, ServerWorld world ) {
		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( creeper, world );
		if( MajruszsDifficulty.RANDOM.nextDouble() >= Config.getChance( Config.Chances.CREEPER_EFFECTS ) * clampedRegionalDifficulty )
			return;

		Effect randomEffect = effects[ MajruszsDifficulty.RANDOM.nextInt( effects.length ) ];
		creeper.addPotionEffect( new EffectInstance( randomEffect, effectDurationInTicks, 0 ) );
	}
}

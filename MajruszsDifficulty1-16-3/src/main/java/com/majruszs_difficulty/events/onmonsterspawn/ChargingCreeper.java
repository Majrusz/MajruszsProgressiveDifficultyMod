package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.server.ServerWorld;

public class ChargingCreeper {
	protected static final double creeperChargingChance = 0.125D;

	public static void tryToChargeCreeper( CreeperEntity creeper, ServerWorld world ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() < creeperChargingChance * MajruszsHelper.getClampedRegionalDifficulty( creeper, world ) ) {
			LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
			if( lightningBolt != null )
				creeper.func_241841_a( world, lightningBolt );

			creeper.extinguish();
		}
	}
}

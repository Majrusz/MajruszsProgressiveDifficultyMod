package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.server.ServerWorld;

public class ChargingCreeper {
	public static void tryToChargeCreeper( CreeperEntity creeper, ServerWorld world ) {
		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( creeper, world );
		if( MajruszsDifficulty.RANDOM.nextDouble() >= Config.getChance( Config.Chances.CREEPER_CHARGED ) * clampedRegionalDifficulty )
			return;

		LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt != null )
			creeper.func_241841_a( world, lightningBolt );

		creeper.extinguish();
	}
}

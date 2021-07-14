package com.majruszs_difficulty.features.on_death;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/** Whenever mob dies from any source while the Infested effect is active then it spawns a Parasite. */
public class SpawnParasitesOnDeath extends OnDeathBase {
	private static final String CONFIG_NAME = "ParasiteDeathSpawn";
	private static final String CONFIG_COMMENT = "Spawns Parasites when mob died while having a Infested effect active.";

	public SpawnParasitesOnDeath() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.NORMAL, false );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event ) {
		EffectInstance effect = target.getActivePotionEffect( Instances.INFESTED );
		if( effect != null )
			Instances.INFESTED.spawnParasites( effect.getAmplifier(), target, ( ServerWorld )target.world );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return super.shouldBeExecuted( attacker, target, damageSource ) && target.isPotionActive( Instances.INFESTED );
	}
}

package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.blocks.EndShardOre;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Making all nearby endermans to attack player when it hits any enderman. */
public class TriggerAllEndermansOnAttack extends WhenDamagedBaseOld {
	private static final String CONFIG_NAME = "EndermanAttack";
	private static final String CONFIG_COMMENT = "Makes all nearby enderman target player when it hits any of them.";

	public TriggerAllEndermansOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameStage.Stage.MASTER );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( attacker != null )
			EndShardOre.focusNearbyEndermansOnEntity( attacker, 250.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker != null && target instanceof EnderMan && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
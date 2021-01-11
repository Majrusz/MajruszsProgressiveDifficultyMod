package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.item.TridentItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Making Drowned trident attacks have a chance to spawn lightning. */
public class DrownedLightningOnAttack extends WhenDamagedBase {
	public DrownedLightningOnAttack() {
		super( GameState.State.NORMAL, true );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isDrowned = attacker instanceof DrownedEntity;
		boolean isDrownedHoldingTrident = isDrowned && attacker.getHeldItemMainhand()
			.getItem() instanceof TridentItem;

		return isDrownedHoldingTrident && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	public void whenDamaged( LivingEntity target ) {
		if( !MajruszsHelper.tryChance( calculateChance( target ) ) )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();
		LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt == null )
			return;

		lightningBolt.moveForced( target.getPosX(), target.getPosY(), target.getPosZ() );
		world.addEntity( lightningBolt );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.DROWNED_LIGHTNING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.DROWNED_LIGHTNING );
	}
}
package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.Random;
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
	private static final String CONFIG_NAME = "DrownedLightning";
	private static final String CONFIG_COMMENT = "Drowned attacks with trident spawn lightning bolt.";

	public DrownedLightningOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, GameState.State.EXPERT, true );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, float damage ) {
		if( !Random.tryChance( calculateChance( target ) ) )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();
		LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt == null )
			return;

		lightningBolt.moveForced( target.getPosX(), target.getPosY(), target.getPosZ() );
		world.addEntity( lightningBolt );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isDrowned = attacker instanceof DrownedEntity;
		boolean isDrownedHoldingTrident = isDrowned && attacker.getHeldItemMainhand()
			.getItem() instanceof TridentItem;

		return isDrownedHoldingTrident && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameStage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Making Drowned trident attacks have a chance to spawn lightning. */
public class DrownedLightningOnAttack extends ChanceWhenDamagedBaseOld {
	private static final String CONFIG_NAME = "DrownedLightning";
	private static final String CONFIG_COMMENT = "Drowned attacks with trident spawn lightning bolt.";

	public DrownedLightningOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, GameStage.Stage.EXPERT, true );
	}

	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		if( !tryChance( target ) )
			return;

		ServerLevel world = ( ServerLevel )target.level;
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt == null )
			return;

		lightningBolt.absMoveTo( target.getX(), target.getY(), target.getZ() );
		world.addFreshEntity( lightningBolt );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isDrowned = attacker instanceof Drowned;
		ItemStack heldItemStack = attacker != null ? attacker.getMainHandItem() : null;
		boolean isDrownedHoldingTrident = isDrowned && heldItemStack.getItem() instanceof TridentItem;

		return isDrownedHoldingTrident && super.shouldBeExecuted( attacker, target, damageSource );
	}
}
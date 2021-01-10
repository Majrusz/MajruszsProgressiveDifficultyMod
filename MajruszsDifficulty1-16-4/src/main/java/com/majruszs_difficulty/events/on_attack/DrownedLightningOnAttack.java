package com.majruszs_difficulty.events.on_attack;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

/** Making Drowned trident attacks have a chance to spawn lightning. */
public class DrownedLightningOnAttack extends OnAttackBase {
	public DrownedLightningOnAttack() {
		super( DrownedEntity.class, GameState.Mode.NORMAL, true );
	}

	@Override
	public void onAttack( LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( !MajruszsHelper.tryChance( calculateChance( target ) ) )
			return;

		ItemStack attackerItemStack = attacker.getHeldItemMainhand();
		if( !( attackerItemStack.getItem() instanceof TridentItem ) )
			return;

		ServerWorld world = ( ServerWorld )attacker.getEntityWorld();
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
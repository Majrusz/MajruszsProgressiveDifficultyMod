package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.renderers.OceanShieldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Shield that reflects part of the damage back to the attacker. */
@Mod.EventBusSubscriber
public class OceanShieldItem extends ShieldItem {
	public OceanShieldItem() {
		super( ( new Properties() ).maxDamage( 555 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP )
			.setISTER( ()->OceanShieldRenderer::new ) );
	}

	@Override
	public boolean getIsRepairable( ItemStack toRepair, ItemStack repair ) {
		return Items.PRISMARINE_SHARD.equals( repair.getItem() ) || Items.PRISMARINE_CRYSTALS.equals( repair.getItem() );
	}

	@Override
	public boolean isShield( ItemStack itemStack, LivingEntity entity ) {
		return true;
	}

	@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();
		OceanShieldItem oceanShieldItem = Instances.OCEAN_SHIELD_ITEM;
		ItemStack mainHandItemStack = target.getHeldItem( Hand.MAIN_HAND ), offHandItemStack = target.getHeldItem( Hand.OFF_HAND );
		boolean isBlockingWithShield = target.isHandActive();

		if( oceanShieldItem.equals( mainHandItemStack.getItem() ) ) {
			if( isBlockingWithShield )
				reflectDamage( event.getSource(), target, mainHandItemStack, Hand.MAIN_HAND );
		} else if( oceanShieldItem.equals( offHandItemStack.getItem() ) ) {
			if( isBlockingWithShield )
				reflectDamage( event.getSource(), target, offHandItemStack, Hand.OFF_HAND );
		}
	}

	/** Damages target that attack the player. */
	private static void reflectDamage( DamageSource damageSource, LivingEntity target, ItemStack shieldItem, Hand hand ) {
		if( !( damageSource.getImmediateSource() instanceof LivingEntity ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getImmediateSource();
		attacker.attackEntityFrom( DamageSource.causeThornsDamage( target ), 2.0f );
		shieldItem.damageItem( 1, target, livingEntity->livingEntity.sendBreakAnimation( hand ) );
	}

	public int getItemEnchantability() {
		return 1;
	}
}

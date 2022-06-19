package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.renderers.OceanShieldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Shield that reflects part of the damage back to the attacker. */
@Mod.EventBusSubscriber
public class OceanShieldItem extends ShieldItem {
	public OceanShieldItem() {
		super( new Properties().durability( 555 ).rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ) );
	}

	@Override
	public boolean isValidRepairItem( ItemStack toRepair, ItemStack repair ) {
		return Items.PRISMARINE_SHARD.equals( repair.getItem() ) || Items.PRISMARINE_CRYSTALS.equals( repair.getItem() );
	}

	@Override
	public boolean canPerformAction( ItemStack stack, ToolAction toolAction ) {
		return toolAction.equals( ToolActions.SHIELD_BLOCK );
	}

	@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();
		OceanShieldItem oceanShieldItem = Registries.OCEAN_SHIELD.get();
		ItemStack mainHandItemStack = target.getItemInHand( InteractionHand.MAIN_HAND ), offHandItemStack = target.getItemInHand( InteractionHand.OFF_HAND );
		boolean isBlockingWithShield = target.isBlocking();
		if( !isBlockingWithShield )
			return;

		if( oceanShieldItem.equals( mainHandItemStack.getItem() ) ) {
			reflectDamage( event.getSource(), target, mainHandItemStack, InteractionHand.MAIN_HAND );
		} else if( oceanShieldItem.equals( offHandItemStack.getItem() ) ) {
			reflectDamage( event.getSource(), target, offHandItemStack, InteractionHand.OFF_HAND );
		}
	}

	private static void reflectDamage( DamageSource damageSource, LivingEntity target, ItemStack shieldItem, InteractionHand hand ) {
		if( !( damageSource.getDirectEntity() instanceof LivingEntity attacker ) )
			return;

		attacker.hurt( DamageSource.thorns( target ), 3.0f );
		shieldItem.hurtAndBreak( 1, target, livingEntity->livingEntity.broadcastBreakEvent( hand ) );
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public void initializeClient( java.util.function.Consumer< net.minecraftforge.client.IItemRenderProperties > consumer ) {
		Minecraft minecraft = Minecraft.getInstance();

		consumer.accept( new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return new OceanShieldRenderer( minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels() );
			}
		} );
	}
}

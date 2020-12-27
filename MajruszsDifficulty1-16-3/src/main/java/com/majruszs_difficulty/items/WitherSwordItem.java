package com.majruszs_difficulty.items;

import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WitherSwordItem extends SwordItem {
	public static final int witherDuration = MajruszsHelper.secondsToTicks( 10.0 );

	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 1, -2.4f, ( new Item.Properties() ).group( ItemGroup.COMBAT ) );
	}

	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		DamageSource source = event.getSource();
		if( !( source.getTrueSource() instanceof LivingEntity ) )
			return;

		ItemStack attackerItemStack = ( ( LivingEntity )source.getTrueSource() ).getHeldItemMainhand();
		if( attackerItemStack.getItem() instanceof WitherSwordItem ) {
			LivingEntity target = event.getEntityLiving();

			target.addPotionEffect( new EffectInstance( Effects.WITHER, witherDuration, 0 ) );
		}
	}
}

package com.majruszs_difficulty.items;

import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class BandageItem extends Item {
	public BandageItem() {
		super( ( new Properties() ).maxStackSize( 16 )
			.group( RegistryHandler.ITEM_GROUP ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		removeBleedingIfPossible( itemStack, player, player );

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.bandage.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}

	@SubscribeEvent
	public static void onRightClick( PlayerInteractEvent.EntityInteract event ) {
		removeBleedingIfPossible( event.getItemStack(), event.getPlayer(), event.getEntityLiving() );
	}

	protected static void removeBleedingIfPossible( ItemStack bandage, PlayerEntity player, LivingEntity target ) {
		if( !( target.isPotionActive( RegistryHandler.BLEEDING.get() ) && bandage.getItem() instanceof BandageItem && bandage.getCount() > 0 ) )
			return;

		if( !player.abilities.isCreativeMode )
			bandage.shrink( 1 );

		player.addStat( Stats.ITEM_USED.get( bandage.getItem() ) );

		target.removeActivePotionEffect( RegistryHandler.BLEEDING.get() );
		target.world.playSound( null, target.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f );
	}
}

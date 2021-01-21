package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.mlib.TimeConverter;
import com.mlib.effects.EffectHelper;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/** Boots that increases movement speed. */
@Mod.EventBusSubscriber
public class HermesBootsItem extends DyeableArmorItem {
	public HermesBootsItem() {
		super( CustomArmorMaterial.HERMES, EquipmentSlotType.FEET, ( new Properties() ).maxStackSize( 16 )
			.group( Instances.ITEM_GROUP ) );
	}

	/** Adding tooltip with information for what bandage is used. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.hermes_boots.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}

	@SubscribeEvent
	@OnlyIn( Dist.CLIENT )
	public static void onColorsInit( ColorHandlerEvent.Item event ) {
		ItemColors itemColors = event.getItemColors();
		itemColors.register( ( stack, color )->color > 0 ? -1 : ( ( IDyeableArmorItem )stack.getItem() ).getColor( stack ), Instances.HERMES_BOOTS_ITEM );
	}
}

package com.majruszs_difficulty.features.end_items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.items.*;
import com.mlib.CommonHelper;
import com.mlib.TimeConverter;
import com.mlib.attributes.AttributeHandler;
import com.mlib.client.ClientHelper;
import com.mlib.effects.EffectHelper;
import com.mlib.features.FarmlandTiller;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/** Class with common functions/variables for end items special functionalities. */
@Mod.EventBusSubscriber
public class EndItems {
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "e8242b56-b5a6-4ad9-9159-f9089ecf3165", "EndSetHealthBonus",
		Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION
	);

	static {
		FarmlandTiller.registerList.add( new FarmlandTiller.Register() {
			@Override
			public boolean shouldBeExecuted( ServerLevel serverLevel, Player player, ItemStack itemStack ) {
				return itemStack.getItem() instanceof HoeItem;
			}
		} );
	}

	/** Returns whether item is either end tool or end sword. */
	public static boolean isEndItem( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndAxeItem || item instanceof EndPickaxeItem || item instanceof EndShovelItem || item instanceof EndSwordItem;
	}

	/** Returns whether item is should have increased chance for inflicting Bleeding effect. */
	public static boolean haveExtraBleedingChance( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndAxeItem || item instanceof EndSwordItem;
	}

	/** Returns whether item can inflicts Levitation effect. */
	public static boolean canInflictLevitation( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndPickaxeItem || item instanceof EndShovelItem;
	}

	public static int getAmountOfEndArmorPieces( Player player ) {
		return ( hasEndHelmet( player ) ? 1 : 0 ) + ( hasEndChestplate( player ) ? 1 : 0 ) + ( hasEndLeggings( player ) ? 1 : 0 ) + ( hasEndBoots(
			player ) ? 1 : 0
		);
	}

	public static boolean hasEndHelmet( Player player ) {
		return hasItem( player, EquipmentSlot.HEAD, EndHelmetItem.class );
	}

	public static boolean hasEndChestplate( Player player ) {
		return hasItem( player, EquipmentSlot.CHEST, EndChestplateItem.class );
	}

	public static boolean hasEndLeggings( Player player ) {
		return hasItem( player, EquipmentSlot.LEGS, EndLeggingsItem.class );
	}

	public static boolean hasEndBoots( Player player ) {
		return hasItem( player, EquipmentSlot.FEET, EndBootsItem.class );
	}

	protected static < Type > boolean hasItem( Player player, EquipmentSlot equipmentSlot, Class< Type > itemType ) {
		return itemType.isInstance( player.getItemBySlot( equipmentSlot ).getItem() );
	}

	@OnlyIn( Dist.CLIENT )
	public static void addEndArmorTooltip( Player player, List< Component > tooltip ) {
		if( ClientHelper.isShiftDown() ) {
			int equippedItems = getAmountOfEndArmorPieces( player );
			String endSetText = new TranslatableComponent( Keys.END_SET_TOOLTIP ).getString();

			tooltip.add( new TranslatableComponent( Keys.SET_TOOLTIP, endSetText, equippedItems, 4 ).withStyle( ChatFormatting.GRAY ) );
			tooltip.add( getFormattedItemName( player, Instances.END_HELMET_ITEM, EndItems::hasEndHelmet ) );
			tooltip.add( getFormattedItemName( player, Instances.END_CHESTPLATE_ITEM, EndItems::hasEndChestplate ) );
			tooltip.add( getFormattedItemName( player, Instances.END_LEGGINGS_ITEM, EndItems::hasEndLeggings ) );
			tooltip.add( getFormattedItemName( player, Instances.END_BOOTS_ITEM, EndItems::hasEndBoots ) );

			MajruszsHelper.addEmptyLine( tooltip );
			tooltip.add( new TranslatableComponent( Keys.BONUS_TOOLTIP ).withStyle( ChatFormatting.GRAY ) );
			tooltip.add( getFormattedBonus( player, Keys.END_BONUS_2_TOOLTIP, 2, EndItems::hasEndHelmet ) );
			tooltip.add( getFormattedBonus( player, Keys.END_BONUS_3_TOOLTIP, 3 ) );
			tooltip.add( getFormattedBonus( player, Keys.END_BONUS_4_TOOLTIP, 4 ) );
		} else {
			MajruszsHelper.addMoreDetailsText( tooltip );
		}
	}

	protected static MutableComponent getItemNameCopy( Item item ) {
		return item.getDescription().copy();
	}

	protected static MutableComponent getFormattedItemName( Player player, Item item, IItemChecker itemChecker ) {
		MutableComponent component = new TextComponent( "  " );

		return component.append(
			getItemNameCopy( item ).withStyle( itemChecker.check( player ) ? ChatFormatting.DARK_PURPLE : ChatFormatting.DARK_GRAY ) );
	}

	protected static MutableComponent getFormattedBonus( Player player, String translationKey, int amountRequired ) {
		return getFormattedBonus( player, translationKey, amountRequired, p -> true );
	}

	protected static MutableComponent getFormattedBonus( Player player, String translationKey, int amountRequired, IItemChecker extraCondition ) {
		boolean meetConditions = getAmountOfEndArmorPieces( player ) >= amountRequired && extraCondition.check( player );
		ChatFormatting textFormat = meetConditions ? ChatFormatting.DARK_PURPLE : ChatFormatting.DARK_GRAY;

		return new TranslatableComponent( translationKey, amountRequired, 4 ).withStyle( textFormat );
	}

	@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		Player player = CommonHelper.castIfPossible( Player.class, event.getEntityLiving() );
		if( player != null && event.getAmount() >= 1 && getAmountOfEndArmorPieces( player ) >= 3 )
			EffectHelper.applyEffectIfPossible( player, MobEffects.MOVEMENT_SPEED, TimeConverter.secondsToTicks( 12.0 ), 0 );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		Player player = CommonHelper.castIfPossible( Player.class, event.getEntityLiving() );

		if( player != null )
			ATTRIBUTE_HANDLER.setValueAndApply( player, getAmountOfEndArmorPieces( player ) >= 4 ? 4.0 : 0.0 );
	}

	public static class Keys {
		public static final String HASTE_TOOLTIP = "majruszs_difficulty.end_items.haste_tooltip";
		public static final String BLEED_TOOLTIP = "majruszs_difficulty.end_items.bleed_tooltip";
		public static final String LEVITATION_TOOLTIP = "majruszs_difficulty.end_items.levitation_tooltip";
		public static final String TILL_TOOLTIP = "majruszs_difficulty.end_items.till_tooltip";
		public static final String SET_TOOLTIP = "majruszs_difficulty.items.set_list_tooltip";
		public static final String BONUS_TOOLTIP = "majruszs_difficulty.items.set_bonus_tooltip";
		public static final String END_SET_TOOLTIP = "majruszs_difficulty.end_items.armor_set";
		public static final String END_BONUS_2_TOOLTIP = "majruszs_difficulty.end_items.armor_set_bonus_2";
		public static final String END_BONUS_3_TOOLTIP = "majruszs_difficulty.end_items.armor_set_bonus_3";
		public static final String END_BONUS_4_TOOLTIP = "majruszs_difficulty.end_items.armor_set_bonus_4";
	}

	protected interface IItemChecker {
		boolean check( Player player );
	}
}

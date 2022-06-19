package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.attributes.AttributeHandler;
import com.mlib.effects.EffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EnderiumSet extends BaseSet {
	private static final ItemData ITEM_1 = new ItemData( Registries.ENDERIUM_HELMET, EquipmentSlot.HEAD );
	private static final ItemData ITEM_2 = new ItemData( Registries.ENDERIUM_CHESTPLATE, EquipmentSlot.CHEST );
	private static final ItemData ITEM_3 = new ItemData( Registries.ENDERIUM_LEGGINGS, EquipmentSlot.LEGS );
	private static final ItemData ITEM_4 = new ItemData( Registries.ENDERIUM_BOOTS, EquipmentSlot.FEET );
	private static final BonusData BONUS_1 = new BonusData( 2, "majruszsdifficulty.sets.enderium.bonus_2", ( set, player ) -> ITEM_1.hasItemEquipped( player ) );
	private static final BonusData BONUS_2 = new BonusData( 3, "majruszsdifficulty.sets.enderium.bonus_3" );
	private static final BonusData BONUS_3 = new BonusData( 4, "majruszsdifficulty.sets.enderium.bonus_4" );
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "e8242b56-b5a6-4ad9-9159-f9089ecf3165", "EndSetHealthBonus", Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION );

	public EnderiumSet() {
		super( new ItemData[]{ ITEM_1, ITEM_2, ITEM_3, ITEM_4 }, new BonusData[]{ BONUS_1, BONUS_2, BONUS_3 }, ChatFormatting.DARK_PURPLE, "majruszsdifficulty.sets.enderium.name" );
	}

	@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		if( event.getEntityLiving() instanceof Player player && event.getAmount() >= 1 && Registries.ENDERIUM_SET.countSetItems( player ) >= 3 )
			EffectHelper.applyEffectIfPossible( player, MobEffects.MOVEMENT_SPEED, Utility.secondsToTicks( 12.0 ), 0 );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		if( event.getEntityLiving() instanceof Player player )
			ATTRIBUTE_HANDLER.setValueAndApply( player, Registries.ENDERIUM_SET.countSetItems( player ) >= 4 ? 4.0 : 0.0 );
	}
}

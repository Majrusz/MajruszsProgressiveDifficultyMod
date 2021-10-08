package com.majruszs_difficulty.features.item_sets;

import com.majruszs_difficulty.Instances;
import com.mlib.CommonHelper;
import com.mlib.TimeConverter;
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
public class EndSet extends BaseSet {
	private static final ItemData ITEM_1 = new ItemData( Instances.END_HELMET_ITEM, EquipmentSlot.HEAD );
	private static final ItemData ITEM_2 = new ItemData( Instances.END_CHESTPLATE_ITEM, EquipmentSlot.CHEST );
	private static final ItemData ITEM_3 = new ItemData( Instances.END_LEGGINGS_ITEM, EquipmentSlot.LEGS );
	private static final ItemData ITEM_4 = new ItemData( Instances.END_BOOTS_ITEM, EquipmentSlot.FEET );
	private static final BonusData BONUS_1 = new BonusData( 2, "majruszs_difficulty.sets.end.bonus_2", ITEM_1::hasItemEquipped );
	private static final BonusData BONUS_2 = new BonusData( 3, "majruszs_difficulty.sets.end.bonus_3" );
	private static final BonusData BONUS_3 = new BonusData( 4, "majruszs_difficulty.sets.end.bonus_4" );
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "e8242b56-b5a6-4ad9-9159-f9089ecf3165", "EndSetHealthBonus",
		Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION
	);

	public EndSet() {
		this.itemData = new ItemData[]{ ITEM_1, ITEM_2, ITEM_3, ITEM_4 };
		this.bonusData = new BonusData[]{ BONUS_1, BONUS_2, BONUS_3 };
		this.chatFormatting = ChatFormatting.DARK_PURPLE;
		this.setTranslationKey = "majruszs_difficulty.sets.end.name";

		SET_LIST.add( this );
	}

	@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		EndSet endSet = Instances.END_SET;
		Player player = CommonHelper.castIfPossible( Player.class, event.getEntityLiving() );

		if( player != null && event.getAmount() >= 1 && endSet.countSetItems( player ) >= 3 )
			EffectHelper.applyEffectIfPossible( player, MobEffects.MOVEMENT_SPEED, TimeConverter.secondsToTicks( 12.0 ), 0 );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		EndSet endSet = Instances.END_SET;
		Player player = CommonHelper.castIfPossible( Player.class, event.getEntityLiving() );

		if( player != null )
			ATTRIBUTE_HANDLER.setValueAndApply( player, endSet.countSetItems( player ) >= 4 ? 4.0 : 0.0 );
	}
}

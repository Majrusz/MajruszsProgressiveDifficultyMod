package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.Registries;
import com.mlib.attributes.AttributeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EndSet extends BaseSet {
	private final ItemData ITEM_1 = new ItemData( Registries.END_HELMET.get(), EquipmentSlot.HEAD );
	private final ItemData ITEM_2 = new ItemData( Registries.END_CHESTPLATE.get(), EquipmentSlot.CHEST );
	private final ItemData ITEM_3 = new ItemData( Registries.END_LEGGINGS.get(), EquipmentSlot.LEGS );
	private final ItemData ITEM_4 = new ItemData( Registries.END_BOOTS.get(), EquipmentSlot.FEET );
	private final BonusData BONUS_1 = new BonusData( 2, "majruszsdifficulty.sets.end.bonus_2", ITEM_1::hasItemEquipped );
	private final BonusData BONUS_2 = new BonusData( 3, "majruszsdifficulty.sets.end.bonus_3" );
	private final BonusData BONUS_3 = new BonusData( 4, "majruszsdifficulty.sets.end.bonus_4" );
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "e8242b56-b5a6-4ad9-9159-f9089ecf3165", "EndSetHealthBonus", Attributes.MAX_HEALTH,
		AttributeModifier.Operation.ADDITION
	);

	public EndSet() {
		this.itemData = new ItemData[]{
			ITEM_1,
			ITEM_2,
			ITEM_3,
			ITEM_4
		};
		this.bonusData = new BonusData[]{
			BONUS_1,
			BONUS_2,
			BONUS_3
		};
		this.chatFormatting = ChatFormatting.DARK_PURPLE;
		this.setTranslationKey = "majruszsdifficulty.sets.end.name";

		SET_LIST.add( this );
	}

	/*@SubscribeEvent
	public static void onHurt( LivingHurtEvent event ) {
		EndSet endSet = Registries.ItemSets.END;
		Player player = Utility.castIfPossible( Player.class, event.getEntityLiving() );

		if( player != null && event.getAmount() >= 1 && endSet.countSetItems( player ) >= 3 )
			EffectHelper.applyEffectIfPossible( player, MobEffects.MOVEMENT_SPEED, Utility.secondsToTicks( 12.0 ), 0 );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		EndSet endSet = Registries.ItemSets.END;
		Player player = Utility.castIfPossible( Player.class, event.getEntityLiving() );

		if( player != null )
			ATTRIBUTE_HANDLER.setValueAndApply( player, endSet.countSetItems( player ) >= 4 ? 4.0 : 0.0 );
	}*/
}

package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.Instances;
import com.mlib.LevelHelper;
import com.mlib.attributes.AttributeHandler;
import com.mlib.time.TimeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OceanSet extends BaseSet {
	private static final ItemData ITEM_1 = new ItemData( Items.TRIDENT, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND );
	private static final ItemData ITEM_2 = new ItemData( Instances.OCEAN_SHIELD_ITEM, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND );
	private static final BonusData BONUS_1 = new BonusData( 2, "majruszsdifficulty.sets.ocean.bonus_2_1" );
	private static final BonusData BONUS_2 = new BonusData( 2, "majruszsdifficulty.sets.ocean.bonus_2_2" );
	private static final AttributeHandler DAMAGE_BONUS = new AttributeHandler( "d7f6c4ae-7cb0-4d5b-87a1-7e9a7a4af154", "OceanSetDamageBonus",
		Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_TOTAL
	);
	private static final AttributeHandler MOVEMENT_BONUS = new AttributeHandler( "c588a4c0-bf17-4e3b-bdb6-1de3211efeab", "OceanSetMovementBonus",
		Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL
	);

	public OceanSet() {
		this.itemData = new ItemData[]{ ITEM_1, ITEM_2 };
		this.bonusData = new BonusData[]{ BONUS_1, BONUS_2 };
		this.chatFormatting = ChatFormatting.AQUA;
		this.setTranslationKey = "majruszsdifficulty.sets.ocean.name";

		SET_LIST.add( this );
	}

	@SubscribeEvent
	public static void onTick( TickEvent.PlayerTickEvent event ) {
		OceanSet oceanSet = Instances.OCEAN_SET;
		Player player = event.player;
		if( !TimeHelper.isEndPhase( event ) || !TimeHelper.hasServerTicksPassed( 4 ) )
			return;

		boolean hasContactWithWater = LevelHelper.isEntityOutsideWhenItIsRaining( player ) || player.isInWater();
		boolean hasFullOceanSet = oceanSet.countSetItems( player ) >= 2;
		DAMAGE_BONUS.setValueAndApply( player, hasFullOceanSet && hasContactWithWater ? 0.2 : 0 );
		MOVEMENT_BONUS.setValueAndApply( player, hasFullOceanSet && hasContactWithWater ? 0.3 : 0 );
	}
}

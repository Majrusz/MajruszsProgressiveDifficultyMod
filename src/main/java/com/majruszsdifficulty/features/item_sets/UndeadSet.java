package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.items.UndeadArmorItem;
import com.mlib.attributes.AttributeHandler;
import com.mlib.time.TimeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class UndeadSet extends BaseSet {
	private static final ItemData ITEM_1 = new ItemData( UndeadArmorItem.IS_SET_ITEM, ()->Component.translatable( UndeadArmorItem.HELMET_ID ), EquipmentSlot.HEAD );
	private static final ItemData ITEM_2 = new ItemData( UndeadArmorItem.IS_SET_ITEM, ()->Component.translatable( UndeadArmorItem.CHESTPLATE_ID ), EquipmentSlot.CHEST );
	private static final ItemData ITEM_3 = new ItemData( UndeadArmorItem.IS_SET_ITEM, ()->Component.translatable( UndeadArmorItem.LEGGINGS_ID ), EquipmentSlot.LEGS );
	private static final ItemData ITEM_4 = new ItemData( UndeadArmorItem.IS_SET_ITEM, ()->Component.translatable( UndeadArmorItem.BOOTS_ID ), EquipmentSlot.FEET );
	private static final BonusData BONUS_1 = new BonusData( 4, "majruszsdifficulty.sets.undead.bonus_4_1", Parameter.asPercent( 0.4f ) );
	private static final BonusData BONUS_2 = new BonusData( 4, "majruszsdifficulty.sets.undead.bonus_4_2", Parameter.asPercent( 0.2f ) );
	private static final AttributeHandler MOVEMENT_BONUS = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "UndeadSetMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );

	public UndeadSet() {
		super( new ItemData[]{ ITEM_1, ITEM_2, ITEM_3, ITEM_4 }, new BonusData[]{ BONUS_1, BONUS_2 }, ChatFormatting.LIGHT_PURPLE, "majruszsdifficulty.sets.undead.name" );
	}

	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		if( !( event.getEntityLiving() instanceof Player player ) || !( event.getSource().getEntity() instanceof Mob mob ) )
			return;

		boolean hasFullUndeadSet = Registries.UNDEAD_SET.countSetItems( player ) >= 4;
		if( mob.getMobType() == MobType.UNDEAD && hasFullUndeadSet ) {
			event.setAmount( event.getAmount() * ( 1.0f - BONUS_1.asFloat( 0 ) ) );
		}
	}

	@SubscribeEvent
	public static void onTick( TickEvent.PlayerTickEvent event ) {
		Player player = event.player;
		if( !TimeHelper.isEndPhase( event ) || !TimeHelper.hasServerTicksPassed( 5 ) )
			return;

		boolean hasFullUndeadSet = Registries.UNDEAD_SET.countSetItems( player ) >= 4;
		MOVEMENT_BONUS.setValueAndApply( player, hasFullUndeadSet ? BONUS_2.asFloat( 0 ) : 0 );
	}
}

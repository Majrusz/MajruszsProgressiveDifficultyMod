package com.majruszsdifficulty.itemsets;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnItemEquipped;
import com.mlib.itemsets.BonusData;
import com.mlib.itemsets.ItemData;
import com.mlib.itemsets.ItemSet;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.stream.Stream;

@AutoInstance
public class UndeadSet extends ItemSet {
	static final float BONUS_VALUE_1 = 0.05f;
	static final float BONUS_VALUE_2 = 0.15f;
	static final float BONUS_VALUE_3 = 0.30f;
	static final ItemData ITEM_1 = new ItemData( Registries.TATTERED_HELMET, EquipmentSlot.HEAD );
	static final ItemData ITEM_2 = new ItemData( Registries.TATTERED_CHESTPLATE, EquipmentSlot.CHEST );
	static final ItemData ITEM_3 = new ItemData( Registries.TATTERED_LEGGINGS, EquipmentSlot.LEGS );
	static final ItemData ITEM_4 = new ItemData( Registries.TATTERED_BOOTS, EquipmentSlot.FEET );
	static final BonusData BONUS_1 = new BonusData( 2, "majruszsdifficulty.sets.undead.bonus_2", TextHelper.percent( BONUS_VALUE_1 ) );
	static final BonusData BONUS_2 = new BonusData( 3, "majruszsdifficulty.sets.undead.bonus_3", TextHelper.percent( BONUS_VALUE_2 - BONUS_VALUE_1 ), TextHelper.percent( BONUS_VALUE_2 ) );
	static final BonusData BONUS_3 = new BonusData( 4, "majruszsdifficulty.sets.undead.bonus_4", TextHelper.percent( BONUS_VALUE_3 - BONUS_VALUE_2 ), TextHelper.percent( BONUS_VALUE_3 ) );
	static final AttributeHandler MOVEMENT_BONUS = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "UndeadSetMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );

	public UndeadSet() {
		super( ()->Stream.of( ITEM_1, ITEM_2, ITEM_3, ITEM_4 ), ()->Stream.of( BONUS_1, BONUS_2, BONUS_3 ), ChatFormatting.LIGHT_PURPLE, "majruszsdifficulty.sets.undead.name" );

		new OnDamaged.Context( this::increaseDamage )
			.addCondition( data->data.target instanceof Mob mob && mob.getMobType() == MobType.UNDEAD )
			.addCondition( data->data.attacker != null );

		new OnItemEquipped.Context( this::increaseMovementSpeed )
			.addCondition( data->data.entity instanceof LivingEntity );
	}

	private void increaseDamage( OnDamaged.Data data ) {
		data.event.setAmount( data.event.getAmount() * ( 1.0f + this.getBonus( data.attacker ) ) );
	}

	private void increaseMovementSpeed( OnItemEquipped.Data data ) {
		MOVEMENT_BONUS.setValueAndApply( ( LivingEntity )data.entity, this.getBonus( ( LivingEntity )data.entity ) );
	}

	private float getBonus( LivingEntity entity ) {
		if( BONUS_3.isConditionMet( this, entity ) ) {
			return BONUS_VALUE_3;
		} else if( BONUS_2.isConditionMet( this, entity ) ) {
			return BONUS_VALUE_2;
		} else if( BONUS_1.isConditionMet( this, entity ) ) {
			return BONUS_VALUE_1;
		} else {
			return 0.0f;
		}
	}
}

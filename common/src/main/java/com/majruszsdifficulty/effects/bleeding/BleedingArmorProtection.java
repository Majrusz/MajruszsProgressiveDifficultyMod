package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityEffectCheck;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

public class BleedingArmorProtection {
	private static float BASE_BONUS = 0.09f;
	private static float ARMOR_BONUS = 0.03f;
	private static float TOUGHNESS_BONUS = 0.03f;

	static {
		OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.effect.equals( MajruszsDifficulty.Effects.BLEEDING.get() ) )
			.addCondition( data->Random.check( BleedingArmorProtection.calculateCancelChance( data ) ) );

		OnBleedingTooltip.listen( BleedingArmorProtection::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof ArmorItem );

		Serializables.getStatic( BleedingConfig.class )
			.define( "chance_multiplier_base", Reader.number(), ()->BASE_BONUS, v->BASE_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "chance_multiplier_per_armor", Reader.number(), ()->ARMOR_BONUS, v->ARMOR_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "chance_multiplier_per_armor_toughness", Reader.number(), ()->TOUGHNESS_BONUS, v->TOUGHNESS_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}

	private static float calculateCancelChance( OnEntityEffectCheck data ) {
		float chance = 1.0f;
		for( EquipmentSlot slot : EquipmentSlots.ARMOR ) {
			if( data.entity.getItemBySlot( slot ).getItem() instanceof ArmorItem armorItem ) {
				chance *= 1.0f - BleedingArmorProtection.getArmorMultiplier( armorItem );
			}
		}

		return 1.0f - chance;
	}

	private static void addTooltip( OnBleedingTooltip data ) {
		data.addArmor( LivingEntity.getEquipmentSlotForItem( data.itemStack ), BleedingArmorProtection.getArmorMultiplier( ( ArmorItem )data.itemStack.getItem() ) );
	}

	private static float getArmorMultiplier( ArmorItem item ) {
		return 1.0f - Range.of( 0.0f, 0.9f ).clamp( BASE_BONUS + ARMOR_BONUS * item.getDefense() + TOUGHNESS_BONUS * item.getToughness() );
	}
}

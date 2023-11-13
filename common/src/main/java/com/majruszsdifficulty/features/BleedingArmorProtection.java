package com.majruszsdifficulty.features;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.majruszsdifficulty.data.Config;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityEffectCheck;
import com.mlib.contexts.base.Condition;
import com.mlib.data.Serializables;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

@AutoInstance
public class BleedingArmorProtection {
	private float armorBonus = 0.05f;
	private float toughnessBonus = 0.05f;

	public BleedingArmorProtection() {
		OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.effect.equals( MajruszsDifficulty.BLEEDING.get() ) )
			.addCondition( data->Random.check( this.calculateCancelChance( data ) ) );

		OnBleedingTooltip.listen( this::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof ArmorItem );

		Serializables.get( Config.Bleeding.class )
			.defineFloat( "chance_multiplier_per_armor", s->this.armorBonus, ( s, v )->this.armorBonus = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.defineFloat( "chance_multiplier_per_armor_toughness", s->this.toughnessBonus, ( s, v )->this.toughnessBonus = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}

	private float calculateCancelChance( OnEntityEffectCheck data ) {
		float chance = 1.0f;
		for( EquipmentSlot slot : EquipmentSlots.ARMOR ) {
			if( data.entity.getItemBySlot( slot ).getItem() instanceof ArmorItem armorItem ) {
				chance *= 1.0f - this.getArmorMultiplier( armorItem );
			}
		}

		return 1.0f - chance;
	}

	private void addTooltip( OnBleedingTooltip data ) {
		data.addArmor( LivingEntity.getEquipmentSlotForItem( data.itemStack ), this.getArmorMultiplier( ( ArmorItem )data.itemStack.getItem() ) );
	}

	private float getArmorMultiplier( ArmorItem item ) {
		return 1.0f - Math.min( this.armorBonus * item.getDefense() + this.toughnessBonus * item.getToughness(), 1.0f );
	}
}

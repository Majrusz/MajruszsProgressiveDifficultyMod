package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.OnSoulJarMultiplierGet;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

public class UndeadSet {
	static final ItemSetRequirement HELMET = ItemSetRequirement.of( MajruszsDifficulty.Items.TATTERED_HELMET, EquipmentSlot.HEAD );
	static final ItemSetRequirement CHESTPLATE = ItemSetRequirement.of( MajruszsDifficulty.Items.TATTERED_CHESTPLATE, EquipmentSlot.CHEST );
	static final ItemSetRequirement LEGGINGS = ItemSetRequirement.of( MajruszsDifficulty.Items.TATTERED_LEGGINGS, EquipmentSlot.LEGS );
	static final ItemSetRequirement BOOTS = ItemSetRequirement.of( MajruszsDifficulty.Items.TATTERED_BOOTS, EquipmentSlot.FEET );
	static final ItemSetBonus DURABILITY = ItemSetBonus.any( 3 )
		.component( "majruszsdifficulty.sets.undead.bonus_1", 1 );
	static final ItemSetBonus SOUL_JAR = ItemSetBonus.any( 4 )
		.component( "majruszsdifficulty.sets.undead.bonus_2", TextHelper.translatable( "item.majruszsdifficulty.soul_jar" ) );
	static final ItemSet ITEM_SET = ItemSet.create()
		.component( "majruszsdifficulty.sets.undead.name" )
		.format( ChatFormatting.LIGHT_PURPLE )
		.require( HELMET, CHESTPLATE, LEGGINGS, BOOTS )
		.bonus( DURABILITY, SOUL_JAR );

	static {
		OnEntityDied.listen( UndeadSet::restoreDurability )
			.addCondition( data->data.attacker != null )
			.addCondition( data->data.target instanceof Mob )
			.addCondition( data->ITEM_SET.canTrigger( DURABILITY, data.attacker ) );

		OnSoulJarMultiplierGet.listen( UndeadSet::increaseSoulBonuses )
			.addCondition( data->ITEM_SET.canTrigger( SOUL_JAR, data.entity ) );
	}

	private static void restoreDurability( OnEntityDied data ) {
		data.attacker.getArmorSlots().forEach( itemStack->{
			if( itemStack.isDamaged() ) {
				itemStack.setDamageValue( itemStack.getDamageValue() - 1 );
			}
		} );
	}

	private static void increaseSoulBonuses( OnSoulJarMultiplierGet data ) {
		data.multiplier *= 2.0f;
	}
}

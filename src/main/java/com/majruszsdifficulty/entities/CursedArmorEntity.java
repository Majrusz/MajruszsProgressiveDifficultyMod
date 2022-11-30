package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class CursedArmorEntity extends Monster {
	public static Supplier< EntityType< CursedArmorEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CursedArmorEntity::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 3.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.75 )
			.build();
	}

	public CursedArmorEntity( EntityType< ? extends CursedArmorEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 4 );
	}

	@AutoInstance
	public static class Equip extends GameModifier {
		public Equip() {
			super( Registries.Modifiers.DEFAULT, "", "" );

			OnSpawned.Context onSpawned = new OnSpawned.Context( this::equipArmor );
			onSpawned.addCondition( data->data.target instanceof CursedArmorEntity )
				.addCondition( data->!data.loadedFromDisk );
		}

		private void equipArmor( OnSpawned.Data data ) {
			CursedArmorEntity cursedArmor = ( CursedArmorEntity )data.target;
			cursedArmor.setItemSlot( EquipmentSlot.HEAD, new ItemStack( Items.GOLDEN_HELMET ) );
			cursedArmor.setDropChance( EquipmentSlot.HEAD, 1.0f );
			cursedArmor.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.DIAMOND_SWORD ) );
			cursedArmor.setDropChance( EquipmentSlot.MAINHAND, 1.0f );
			cursedArmor.setItemSlot( EquipmentSlot.OFFHAND, new ItemStack( Items.SHIELD ) );
			cursedArmor.setDropChance( EquipmentSlot.OFFHAND, 1.0f );
		}
	}
}

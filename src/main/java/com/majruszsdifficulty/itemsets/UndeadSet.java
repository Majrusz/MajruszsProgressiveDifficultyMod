package com.majruszsdifficulty.itemsets;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.items.TatteredArmorItem;
import com.majruszsdifficulty.items.TatteredEnhancedArmorItem;
import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.gamemodifiers.contexts.OnFoodPropertiesGet;
import com.mlib.gamemodifiers.contexts.OnItemEquipped;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.itemsets.BonusData;
import com.mlib.itemsets.ItemData;
import com.mlib.itemsets.ItemSet;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;

@AutoInstance
public class UndeadSet extends ItemSet {
	static final float SPEED_BONUS = 0.25f;
	static final int SMITE_BONUS = 2;
	static final FoodProperties FLESH_NO_EFFECT = new FoodProperties.Builder().nutrition( 4 ).saturationMod( 0.1f ).meat().build();
	static final FoodProperties FLESH_EXTRA_HUNGER = new FoodProperties.Builder().nutrition( 4 * 2 ).saturationMod( 0.1f ).meat().build();
	static final AttributeHandler MOVEMENT_ATTRIBUTE = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "UndeadSetMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
	static final ItemData ITEM_1 = new ItemData( Registries.TATTERED_HELMET, EquipmentSlot.HEAD );
	static final ItemData ITEM_2 = new ItemData( UndeadSet::isAnyTatteredChestplate, asDescription( Registries.TATTERED_CHESTPLATE ), EquipmentSlot.CHEST );
	static final ItemData ITEM_3 = new ItemData( UndeadSet::isAnyTatteredLeggings, asDescription( Registries.TATTERED_LEGGINGS ), EquipmentSlot.LEGS );
	static final ItemData ITEM_4 = new ItemData( Registries.TATTERED_BOOTS, EquipmentSlot.FEET );
	static final BonusData BONUS_1 = new BonusData( 3, "majruszsdifficulty.sets.undead.bonus_1", MobEffects.HUNGER.getDisplayName(), Items.ROTTEN_FLESH.getDescription() );
	static final BonusData BONUS_2 = new BonusData( 4, "majruszsdifficulty.sets.undead.bonus_2", Items.ROTTEN_FLESH.getDescription() );
	static final BonusData BONUS_3 = new BonusData( UndeadSet::hasEnhancedTatteredLeggings, "majruszsdifficulty.sets.undead.bonus_3", ( set, bonus )->Component.translatable( "item.majruszsdifficulty.undead_army_belt" ), TextHelper.percent( SPEED_BONUS ) );
	static final BonusData BONUS_4 = new BonusData( UndeadSet::hasEnhancedTatteredChestplate, "majruszsdifficulty.sets.undead.bonus_4", ( set, bonus )->Component.translatable( "item.majruszsdifficulty.undead_army_necklace" ), Enchantments.SMITE.getFullname( SMITE_BONUS ) );

	public UndeadSet() {
		super( ()->Stream.of( ITEM_1, ITEM_2, ITEM_3, ITEM_4 ), ()->Stream.of( BONUS_1, BONUS_2, BONUS_3, BONUS_4 ), ChatFormatting.LIGHT_PURPLE, "majruszsdifficulty.sets.undead.name" );

		new OnFoodPropertiesGet.Context( this::applyRottenFleshBoost )
			.addCondition( data->data.itemStack.getItem().equals( Items.ROTTEN_FLESH ) )
			.addCondition( data->data.entity != null );

		new OnItemEquipped.Context( this::updateMovementSpeedBonus )
			.addCondition( data->data.entity instanceof LivingEntity );

		new OnPreDamaged.Context( this::increaseDamage )
			.addCondition( data->data.target instanceof Mob mob && mob.getMobType() == MobType.UNDEAD )
			.addCondition( data->data.attacker != null )
			.addCondition( data->BONUS_4.isConditionMet( this, data.attacker ) );
	}

	private static boolean isAnyTatteredChestplate( ItemStack itemStack ) {
		return itemStack.getItem() instanceof TatteredArmorItem.Chestplate
			|| itemStack.getItem() instanceof TatteredEnhancedArmorItem.Chestplate;
	}

	private static boolean isAnyTatteredLeggings( ItemStack itemStack ) {
		return itemStack.getItem() instanceof TatteredArmorItem.Leggings
			|| itemStack.getItem() instanceof TatteredEnhancedArmorItem.Leggings;
	}

	private static Supplier< MutableComponent > asDescription( RegistryObject< ? extends Item > item ) {
		return ()->item.get().getDescription().copy();
	}

	private static boolean hasEnhancedTatteredChestplate( ItemSet set, LivingEntity entity ) {
		return entity.getItemBySlot( EquipmentSlot.CHEST ).getItem() instanceof TatteredEnhancedArmorItem.Chestplate;
	}

	private static boolean hasEnhancedTatteredLeggings( ItemSet set, LivingEntity entity ) {
		return entity.getItemBySlot( EquipmentSlot.LEGS ).getItem() instanceof TatteredEnhancedArmorItem.Leggings;
	}

	private void applyRottenFleshBoost( OnFoodPropertiesGet.Data data ) {
		if( BONUS_2.isConditionMet( this, data.entity ) ) {
			data.properties = FLESH_EXTRA_HUNGER;
		} else if( BONUS_1.isConditionMet( this, data.entity ) ) {
			data.properties = FLESH_NO_EFFECT;
		}
	}

	private void increaseDamage( OnPreDamaged.Data data ) {
		data.extraDamage += SMITE_BONUS * 2.5f;
		data.spawnMagicParticles = true;
	}

	private void updateMovementSpeedBonus( OnItemEquipped.Data data ) {
		float speedBonus = BONUS_3.isConditionMet( this, ( LivingEntity )data.entity ) ? SPEED_BONUS : 0.0f;

		MOVEMENT_ATTRIBUTE.setValueAndApply( ( LivingEntity )data.entity, speedBonus );
	}
}

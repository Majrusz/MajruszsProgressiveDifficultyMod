package com.majruszsdifficulty.itemsets;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnChorusFruitTeleport;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnEnderManAnger;
import com.mlib.gamemodifiers.contexts.OnLootLevel;
import com.mlib.itemsets.BonusData;
import com.mlib.itemsets.ItemData;
import com.mlib.itemsets.ItemSet;
import com.mlib.levels.LevelHelper;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.time.Time;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

@AutoInstance
public class EnderiumSet extends ItemSet {
	static final MobEffect[] EFFECTS = new MobEffect[]{
		MobEffects.ABSORPTION,
		MobEffects.DAMAGE_BOOST,
		MobEffects.DAMAGE_RESISTANCE,
		MobEffects.FIRE_RESISTANCE,
		MobEffects.JUMP,
		MobEffects.MOVEMENT_SPEED,
		MobEffects.SATURATION
	};
	static final ItemData ITEM_1 = new ItemData( Registries.ENDERIUM_HELMET, EquipmentSlot.HEAD );
	static final ItemData ITEM_2 = new ItemData( Registries.ENDERIUM_CHESTPLATE, EquipmentSlot.CHEST );
	static final ItemData ITEM_3 = new ItemData( Registries.ENDERIUM_LEGGINGS, EquipmentSlot.LEGS );
	static final ItemData ITEM_4 = new ItemData( Registries.ENDERIUM_BOOTS, EquipmentSlot.FEET );
	static final BonusData BONUS_1 = new BonusData( ( itemSet, entity )->ITEM_1.isEquipped( entity ), "majruszsdifficulty.sets.enderium.bonus_1", ( itemSet, entity )->Component.translatable( "item.majruszsdifficulty.enderium_helmet" ) );
	static final BonusData BONUS_2 = new BonusData( 2, "majruszsdifficulty.sets.enderium.bonus_2" );
	static final BonusData BONUS_3 = new BonusData( 3, "majruszsdifficulty.sets.enderium.bonus_3", Items.CHORUS_FRUIT.getDescription() );
	static final BonusData BONUS_4 = new BonusData( 4, "majruszsdifficulty.sets.enderium.bonus_4" );

	public EnderiumSet() {
		super( ()->Stream.of( ITEM_1, ITEM_2, ITEM_3, ITEM_4 ), ()->Stream.of( BONUS_1, BONUS_2, BONUS_3, BONUS_4 ), ChatFormatting.DARK_PURPLE, "majruszsdifficulty.sets.enderium.name" );

		OnEnderManAnger.listen( this::cancelAnger )
			.addCondition( Condition.predicate( data->BONUS_1.isConditionMet( this, data.player ) ) );

		OnLootLevel.listen( this::increaseLuck )
			.addCondition( Condition.predicate( data->data.attacker != null ) )
			.addCondition( Condition.predicate( data->data.getLevel().dimension() == Level.END ) )
			.addCondition( Condition.predicate( data->BONUS_2.isConditionMet( this, data.attacker ) ) );

		OnChorusFruitTeleport.listen( this::giveRandomPotionEffect )
			.addCondition( Condition.predicate( data->BONUS_3.isConditionMet( this, data.event.getEntityLiving() ) ) );

		OnDeath.listen( this::cancelDeath )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.predicate( data->BONUS_4.isConditionMet( this, data.target ) ) )
			.addCondition( Condition.predicate( data->data.target.getY() < data.target.level().getMinBuildHeight() - 64 ) )
			.addCondition( Condition.predicate( data->data.source.is( DamageTypes.FELL_OUT_OF_WORLD ) ) )
			.addCondition( Condition.predicate( data->data.target instanceof ServerPlayer ) );
	}

	private void cancelAnger( OnEnderManAnger.Data data ) {
		data.event.setCanceled( true );
	}

	private void increaseLuck( OnLootLevel.Data data ) {
		data.event.setLootingLevel( data.event.getLootingLevel() + 1 );
	}

	private void giveRandomPotionEffect( OnChorusFruitTeleport.Data data ) {
		List< MobEffect > notAppliedEffects = Stream.of( EFFECTS )
			.filter( effect->MobEffectHelper.getAmplifier( data.event.getEntityLiving(), effect ) == -1 )
			.toList();

		MobEffectHelper.tryToApply( data.event.getEntityLiving(), Random.nextRandom( !notAppliedEffects.isEmpty() ? notAppliedEffects : List.of( EFFECTS ) ), Utility.minutesToTicks( 1.5 ), 0 );
		data.event.setCanceled( true );
	}

	private void cancelDeath( OnDeath.Data data ) {
		EntityHelper.cheatDeath( data.target, 1.0f, false );
		data.target.setDeltaMovement( 0.0, 0.2, 0.0 );
		LevelHelper.teleportToSpawnPosition( ( ServerPlayer )data.target );
		Time.slider( 3.0, slider->ParticleHandler.PORTAL.spawn( ( ServerLevel )data.target.level(), data.target.position(), ( int )Math.ceil( slider.getRatioLeft() * 5 ) ) );
		data.event.setCanceled( true );
	}
}

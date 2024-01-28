package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnChorusFruitEaten;
import com.majruszlibrary.events.OnEnderManAngered;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.events.OnLootingLevelGet;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class EnderiumSet {
	static final MobEffect[] EFFECTS = new MobEffect[]{
		MobEffects.ABSORPTION,
		MobEffects.DAMAGE_BOOST,
		MobEffects.DAMAGE_RESISTANCE,
		MobEffects.FIRE_RESISTANCE,
		MobEffects.JUMP,
		MobEffects.MOVEMENT_SPEED,
		MobEffects.SATURATION
	};
	static final ItemSetRequirement HELMET = ItemSetRequirement.of( MajruszsDifficulty.ENDERIUM_HELMET_ITEM, EquipmentSlot.HEAD );
	static final ItemSetRequirement CHESTPLATE = ItemSetRequirement.of( MajruszsDifficulty.ENDERIUM_CHESTPLATE_ITEM, EquipmentSlot.CHEST );
	static final ItemSetRequirement LEGGINGS = ItemSetRequirement.of( MajruszsDifficulty.ENDERIUM_LEGGINGS_ITEM, EquipmentSlot.LEGS );
	static final ItemSetRequirement BOOTS = ItemSetRequirement.of( MajruszsDifficulty.ENDERIUM_BOOTS_ITEM, EquipmentSlot.FEET );
	static final ItemSetBonus ENDERMAN_PROTECTION = ItemSetBonus.requires( HELMET )
		.component( "majruszsdifficulty.sets.enderium.bonus_1", TextHelper.translatable( "item.majruszsdifficulty.enderium_helmet" ) );
	static final ItemSetBonus END_LOOTING = ItemSetBonus.any( 2 )
		.component( "majruszsdifficulty.sets.enderium.bonus_2" );
	static final ItemSetBonus CHORUS_FRUIT = ItemSetBonus.any( 3 )
		.component( "majruszsdifficulty.sets.enderium.bonus_3", Items.CHORUS_FRUIT.getDescription() );
	static final ItemSetBonus VOID_PROTECTION = ItemSetBonus.any( 4 )
		.component( "majruszsdifficulty.sets.enderium.bonus_4" );
	static final ItemSet ITEM_SET = ItemSet.create()
		.component( "majruszsdifficulty.sets.enderium.name" )
		.format( ChatFormatting.DARK_PURPLE )
		.require( HELMET, CHESTPLATE, LEGGINGS, BOOTS )
		.bonus( ENDERMAN_PROTECTION, END_LOOTING, CHORUS_FRUIT, VOID_PROTECTION );

	static {
		OnEnderManAngered.listen( OnEnderManAngered::cancelAnger )
			.addCondition( data->ITEM_SET.canTrigger( ENDERMAN_PROTECTION, data.player ) );

		OnLootingLevelGet.listen( EnderiumSet::increaseLooting )
			.addCondition( data->data.source != null )
			.addCondition( data->data.source.getEntity() instanceof LivingEntity )
			.addCondition( data->data.getLevel().dimension() == Level.END )
			.addCondition( data->ITEM_SET.canTrigger( END_LOOTING, ( LivingEntity )data.source.getEntity() ) );

		OnChorusFruitEaten.listen( EnderiumSet::giveRandomEffect )
			.addCondition( data->ITEM_SET.canTrigger( CHORUS_FRUIT, data.entity ) );

		OnEntityDied.listen( EnderiumSet::cancelDeath )
			.addCondition( data->ITEM_SET.canTrigger( VOID_PROTECTION, data.target ) )
			.addCondition( data->data.target.getY() < data.target.getLevel().getMinBuildHeight() - 64 )
			.addCondition( data->data.source.is( DamageTypes.OUT_OF_WORLD ) );
	}

	private static void increaseLooting( OnLootingLevelGet data ) {
		data.level += 1;
	}

	private static void giveRandomEffect( OnChorusFruitEaten data ) {
		List< MobEffect > notAppliedEffects = Stream.of( EFFECTS )
			.filter( effect->EffectHelper.getAmplifier( ()->effect, data.entity ).isEmpty() )
			.toList();
		if( notAppliedEffects.isEmpty() ) {
			notAppliedEffects = List.of( EFFECTS );
		}

		data.entity.addEffect( new MobEffectInstance( Random.next( notAppliedEffects ), TimeHelper.toTicks( 90 ), 0 ) );
		data.cancelTeleport();
	}

	private static void cancelDeath( OnEntityDied data ) {
		if( Side.isLogicalServer() ) {
			ServerPlayer player = ( ServerPlayer )data.target;
			LevelHelper.getSpawnPoint( player )
				.map( spawnPoint->new LevelHelper.SpawnPoint( spawnPoint.level, AnyPos.from( spawnPoint.position ).add( 0.0, 0.5, 0.0 ).vec3() ) )
				.ifPresent( spawnPoint->spawnPoint.teleport( player ) );
		}
		EntityHelper.cheatDeath( data.target );
		data.target.setDeltaMovement( 0.0, 0.5, 0.0 );
		data.cancelDeath();
	}
}

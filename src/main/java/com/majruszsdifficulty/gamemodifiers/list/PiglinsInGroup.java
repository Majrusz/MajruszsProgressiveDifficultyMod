package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Items;

public class PiglinsInGroup extends GameModifier {
	final ItemStackConfig sword = new ItemStackConfig( "SidekickGoldenSword", "Chance for a sidekick to have the Golden Sword.", ()->Items.GOLDEN_SWORD, EquipmentSlot.MAINHAND, 0.4, 0.05, 0.2 );
	final ItemStackConfig helmet = new ItemStackConfig( "LeaderGoldenHelmet", "Chance for a leader to have the Golden Helmet.", ()->Items.GOLDEN_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	final ItemStackConfig chestplate = new ItemStackConfig( "LeaderGoldenChestplate", "Chance for a leader to have the Golden Chestplate.", ()->Items.GOLDEN_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	final ItemStackConfig leggings = new ItemStackConfig( "LeaderGoldenLeggings", "Chance for a leader to have the Golden Leggings.", ()->Items.GOLDEN_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	final ItemStackConfig boots = new ItemStackConfig( "LeaderGoldenBoots", "Chance for a leader to have the Golden Boots.", ()->Items.GOLDEN_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	final MobGroupConfig mobGroups = new MobGroupConfig( "Piglins", ()->EntityType.PIGLIN, 1, 3 );

	public PiglinsInGroup() {
		super( Registries.Modifiers.DEFAULT, "PiglinsInGroup", "Piglins may spawn in groups." );

		this.mobGroups.addLeaderConfigs( this.helmet, this.chestplate, this.leggings, this.boots );
		this.mobGroups.addSidekickConfigs( this.sword );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::spawnGroup );
		onSpawned.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.25, true ) )
			.addCondition( new CustomConditions.IsNotSidekick<>() )
			.addCondition( new CustomConditions.IsNotTooManyMobsNearby<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
			.addCondition( data->data.level != null )
			.addCondition( data->data.target instanceof Piglin )
			.addConfigs( this.sword, this.helmet, this.chestplate, this.leggings, this.boots, this.mobGroups );

		this.addContext( onSpawned );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

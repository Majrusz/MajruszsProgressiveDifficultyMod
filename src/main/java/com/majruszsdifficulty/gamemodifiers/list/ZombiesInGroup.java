package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.Items;

public class ZombiesInGroup extends GameModifier {
	static final ItemStackConfig TORCH = new ItemStackConfig( "SidekickTorch", "Chance for a sidekick to have a Torch (main hand).", ()->Items.TORCH, EquipmentSlot.MAINHAND, 1.0, 0.05 );
	static final ItemStackConfig WOODEN_PICKAXE = new ItemStackConfig( "SidekickWoodenPickaxe", "Chance for a sidekick to have the Wooden Pickaxe (main hand).", ()->Items.WOODEN_PICKAXE, EquipmentSlot.MAINHAND, 0.5, 0.05, 0.1 );
	static final ItemStackConfig STONE_PICKAXE = new ItemStackConfig( "SidekickStonePickaxe", "Chance for a sidekick to have the Stone Pickaxe (main hand).", ()->Items.STONE_PICKAXE, EquipmentSlot.MAINHAND, 0.25, 0.05, 0.1 );
	static final ItemStackConfig IRON_PICKAXE = new ItemStackConfig( "SidekickIronPickaxe", "Chance for a sidekick to have the Iron Pickaxe (main hand).", ()->Items.IRON_PICKAXE, EquipmentSlot.MAINHAND, 0.1, 0.05, 0.2 );
	static final ItemStackConfig COAL = new ItemStackConfig( "SidekickCoal", "Chance for a sidekick to have a Coal (offhand).", ()->Items.COAL, EquipmentSlot.OFFHAND, 0.6, 1 );
	static final ItemStackConfig IRON_INGOT = new ItemStackConfig( "SidekickIronIngot", "Chance for a sidekick to have an Iron Ingot (offhand).", ()->Items.IRON_INGOT, EquipmentSlot.OFFHAND, 0.4, 1 );
	static final ItemStackConfig GOLD_INGOT = new ItemStackConfig( "SidekickGoldIngot", "Chance for a sidekick to have a Gold Ingot (offhand).", ()->Items.GOLD_INGOT, EquipmentSlot.OFFHAND, 0.2, 1 );
	static final ItemStackConfig DIAMOND = new ItemStackConfig( "SidekickDiamond", "Chance for a sidekick to have a Diamond (offhand).", ()->Items.DIAMOND, EquipmentSlot.OFFHAND, 0.1, 1 );
	static final ItemStackConfig IRON_HELMET = new ItemStackConfig( "LeaderIronHelmet", "Chance for a leader to have the Iron Helmet.", ()->Items.IRON_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	static final ItemStackConfig IRON_CHESTPLATE = new ItemStackConfig( "LeaderIronChestplate", "Chance for a leader to have the Iron Chestplate.", ()->Items.IRON_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	static final ItemStackConfig IRON_LEGGINGS = new ItemStackConfig( "LeaderIronLeggings", "Chance for a leader to have the Iron Leggings.", ()->Items.IRON_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	static final ItemStackConfig IRON_BOOTS = new ItemStackConfig( "LeaderIronBoots", "Chance for a leader to have the Iron Boots.", ()->Items.IRON_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	static final MobGroupConfig MOB_GROUPS = new MobGroupConfig( "Zombies", ()->EntityType.ZOMBIE, 1, 3 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( ZombiesInGroup::spawnGroup );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.25 ) );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotSidekick() );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotUndeadArmy() );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Zombie && !( data.target instanceof ZombifiedPiglin ) && data.level != null ) );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->!LevelHelper.isEntityOutside( data.target ) && data.target.position().y < 50.0f ) );
		ON_SPAWNED.addConfigs( TORCH, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, COAL, IRON_INGOT, GOLD_INGOT, DIAMOND, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, MOB_GROUPS );

		MOB_GROUPS.addLeaderConfigs( WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS );
		MOB_GROUPS.addSidekickConfigs( TORCH, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, COAL, IRON_INGOT, GOLD_INGOT, DIAMOND );
	}

	public ZombiesInGroup() {
		super( GameModifier.DEFAULT, "ZombiesInGroup", "Zombies may spawn in groups as miners (only underground).", ON_SPAWNED );
	}

	private static void spawnGroup( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		MOB_GROUPS.spawn( ( PathfinderMob )data.target );
	}
}

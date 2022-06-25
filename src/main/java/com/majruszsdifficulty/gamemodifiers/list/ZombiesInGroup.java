package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.Items;

public class ZombiesInGroup extends GameModifier {
	static final Config.ItemStack TORCH = new Config.ItemStack( "SidekickTorch", "Chance for a sidekick to have a Torch (main hand).", ()->Items.TORCH, EquipmentSlot.MAINHAND, 1.0, 0.05 );
	static final Config.ItemStack WOODEN_PICKAXE = new Config.ItemStack( "SidekickWoodenPickaxe", "Chance for a sidekick to have the Wooden Pickaxe (main hand).", ()->Items.WOODEN_PICKAXE, EquipmentSlot.MAINHAND, 0.5, 0.05, 0.1 );
	static final Config.ItemStack STONE_PICKAXE = new Config.ItemStack( "SidekickStonePickaxe", "Chance for a sidekick to have the Stone Pickaxe (main hand).", ()->Items.STONE_PICKAXE, EquipmentSlot.MAINHAND, 0.25, 0.05, 0.1 );
	static final Config.ItemStack IRON_PICKAXE = new Config.ItemStack( "SidekickIronPickaxe", "Chance for a sidekick to have the Iron Pickaxe (main hand).", ()->Items.IRON_PICKAXE, EquipmentSlot.MAINHAND, 0.1, 0.05, 0.2 );
	static final Config.ItemStack COAL = new Config.ItemStack( "SidekickCoal", "Chance for a sidekick to have a Coal (offhand).", ()->Items.COAL, EquipmentSlot.OFFHAND, 0.6, 1 );
	static final Config.ItemStack IRON_INGOT = new Config.ItemStack( "SidekickIronIngot", "Chance for a sidekick to have an Iron Ingot (offhand).", ()->Items.IRON_INGOT, EquipmentSlot.OFFHAND, 0.4, 1 );
	static final Config.ItemStack GOLD_INGOT = new Config.ItemStack( "SidekickGoldIngot", "Chance for a sidekick to have a Gold Ingot (offhand).", ()->Items.GOLD_INGOT, EquipmentSlot.OFFHAND, 0.2, 1 );
	static final Config.ItemStack DIAMOND = new Config.ItemStack( "SidekickDiamond", "Chance for a sidekick to have a Diamond (offhand).", ()->Items.DIAMOND, EquipmentSlot.OFFHAND, 0.1, 1 );
	static final Config.ItemStack IRON_HELMET = new Config.ItemStack( "LeaderIronHelmet", "Chance for a leader to have the Iron Helmet.", ()->Items.IRON_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	static final Config.ItemStack IRON_CHESTPLATE = new Config.ItemStack( "LeaderIronChestplate", "Chance for a leader to have the Iron Chestplate.", ()->Items.IRON_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	static final Config.ItemStack IRON_LEGGINGS = new Config.ItemStack( "LeaderIronLeggings", "Chance for a leader to have the Iron Leggings.", ()->Items.IRON_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	static final Config.ItemStack IRON_BOOTS = new Config.ItemStack( "LeaderIronBoots", "Chance for a leader to have the Iron Boots.", ()->Items.IRON_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	static final CustomConfigs.MobGroups MOB_GROUPS = new CustomConfigs.MobGroups( "Zombies", ()->EntityType.ZOMBIE, 1, 3 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.25 ) );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotSidekick() );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotUndeadArmy() );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Zombie && !( data.target instanceof ZombifiedPiglin ) ) );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->!LevelHelper.isEntityOutside( data.target ) && data.target.position().y < 50.0f ) );
		ON_SPAWNED.addConfigs( TORCH, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, COAL, IRON_INGOT, GOLD_INGOT, DIAMOND, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, MOB_GROUPS );

		MOB_GROUPS.addLeaderConfigs( WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS );
		MOB_GROUPS.addSidekickConfigs( TORCH, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, COAL, IRON_INGOT, GOLD_INGOT, DIAMOND );
	}

	public ZombiesInGroup() {
		super( GameModifier.DEFAULT, "ZombiesInGroup", "Zombies may spawn in groups as miners (only underground).", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData && spawnedData.level != null ) {
			MOB_GROUPS.spawn( ( PathfinderMob )spawnedData.target );
		}
	}
}

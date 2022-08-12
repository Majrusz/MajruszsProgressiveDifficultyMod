package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.Items;

public class ZombiesInGroup extends GameModifier {
	final ItemStackConfig woodenPickaxe = new ItemStackConfig( "SidekickWoodenPickaxe", "Chance for a sidekick to have the Wooden Pickaxe (main hand).", ()->Items.WOODEN_PICKAXE, EquipmentSlot.MAINHAND, 0.75, 0.05, 0.1 );
	final ItemStackConfig stonePickaxe = new ItemStackConfig( "SidekickStonePickaxe", "Chance for a sidekick to have the Stone Pickaxe (main hand).", ()->Items.STONE_PICKAXE, EquipmentSlot.MAINHAND, 0.5, 0.05, 0.1 );
	final ItemStackConfig ironPickaxe = new ItemStackConfig( "SidekickIronPickaxe", "Chance for a sidekick to have the Iron Pickaxe (main hand).", ()->Items.IRON_PICKAXE, EquipmentSlot.MAINHAND, 0.1, 0.05, 0.2 );
	final ItemStackConfig coal = new ItemStackConfig( "SidekickCoal", "Chance for a sidekick to have a Coal (offhand).", ()->Items.COAL, EquipmentSlot.OFFHAND, 0.6, 1 );
	final ItemStackConfig ironIngot = new ItemStackConfig( "SidekickIronIngot", "Chance for a sidekick to have an Iron Ingot (offhand).", ()->Items.IRON_INGOT, EquipmentSlot.OFFHAND, 0.4, 1 );
	final ItemStackConfig goldIngot = new ItemStackConfig( "SidekickGoldIngot", "Chance for a sidekick to have a Gold Ingot (offhand).", ()->Items.GOLD_INGOT, EquipmentSlot.OFFHAND, 0.2, 1 );
	final ItemStackConfig diamond = new ItemStackConfig( "SidekickDiamond", "Chance for a sidekick to have a Diamond (offhand).", ()->Items.DIAMOND, EquipmentSlot.OFFHAND, 0.1, 1 );
	final ItemStackConfig helmet = new ItemStackConfig( "LeaderIronHelmet", "Chance for a leader to have the Iron Helmet.", ()->Items.IRON_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	final ItemStackConfig chestplate = new ItemStackConfig( "LeaderIronChestplate", "Chance for a leader to have the Iron Chestplate.", ()->Items.IRON_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	final ItemStackConfig leggings = new ItemStackConfig( "LeaderIronLeggings", "Chance for a leader to have the Iron Leggings.", ()->Items.IRON_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	final ItemStackConfig boots = new ItemStackConfig( "LeaderIronBoots", "Chance for a leader to have the Iron Boots.", ()->Items.IRON_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	final MobGroupConfig mobGroups = new MobGroupConfig( "Zombies", ()->EntityType.ZOMBIE, 1, 3 );

	public ZombiesInGroup() {
		super( Registries.Modifiers.DEFAULT, "ZombiesInGroup", "Zombies may spawn in groups as miners (only underground)." );

		mobGroups.addLeaderConfigs( this.woodenPickaxe, this.stonePickaxe, this.ironPickaxe, this.helmet, this.chestplate, this.leggings, this.boots );
		mobGroups.addSidekickConfigs( this.woodenPickaxe, this.stonePickaxe, this.ironPickaxe, this.coal, this.ironIngot, this.goldIngot, this.diamond );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::spawnGroup );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance( 0.25, true ) )
			.addCondition( new CustomConditions.IsNotSidekick() )
			.addCondition( new CustomConditions.IsNotUndeadArmy() )
			.addCondition( new CustomConditions.IsNotTooManyMobsNearby() )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.level != null )
			.addCondition( data->data.target instanceof Zombie && !( data.target instanceof ZombifiedPiglin ) )
			.addCondition( data->!LevelHelper.isEntityOutside( data.target ) && data.target.position().y < 50.0f )
			.addConfigs( this.woodenPickaxe, this.stonePickaxe, this.ironPickaxe, this.coal, this.ironIngot, this.goldIngot, this.diamond )
			.addConfigs( this.helmet, this.chestplate, this.leggings, this.boots, this.mobGroups );

		this.addContext( onSpawned );
	}

	private void spawnGroup( OnSpawnedData data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

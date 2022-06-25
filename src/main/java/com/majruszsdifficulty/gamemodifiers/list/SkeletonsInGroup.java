package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Items;

public class SkeletonsInGroup extends GameModifier {
	static final Config.ItemStack WOODEN_SWORD = new Config.ItemStack( "SidekickWoodenSword", "Chance for a sidekick to have the Wooden Sword.", ()->Items.WOODEN_SWORD, EquipmentSlot.MAINHAND, 0.5, 0.05, 0.2 );
	static final Config.ItemStack STONE_SWORD = new Config.ItemStack( "SidekickStoneSword", "Chance for a sidekick to have the Stone Sword.", ()->Items.STONE_SWORD, EquipmentSlot.MAINHAND, 0.25, 0.05, 0.2 );
	static final Config.ItemStack LEATHER_HELMET = new Config.ItemStack( "LeaderLeatherHelmet", "Chance for a leader to have the Leather Helmet.", ()->Items.LEATHER_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	static final Config.ItemStack LEATHER_CHESTPLATE = new Config.ItemStack( "LeaderLeatherChestplate", "Chance for a leader to have the Leather Chestplate.", ()->Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	static final Config.ItemStack LEATHER_LEGGINGS = new Config.ItemStack( "LeaderLeatherLeggings", "Chance for a leader to have the Leather Leggings.", ()->Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	static final Config.ItemStack LEATHER_BOOTS = new Config.ItemStack( "LeaderLeatherBoots", "Chance for a leader to have the Leather Boots.", ()->Items.LEATHER_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	static final CustomConfigs.MobGroups MOB_GROUPS = new CustomConfigs.MobGroups( "Skeletons", ()->EntityType.SKELETON, 1, 3 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.25 ) );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotSidekick() );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotUndeadArmy() );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Skeleton ) );
		ON_SPAWNED.addConfigs( WOODEN_SWORD, STONE_SWORD, LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, MOB_GROUPS );

		MOB_GROUPS.addLeaderConfigs( LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS );
		MOB_GROUPS.addSidekickConfigs( WOODEN_SWORD, STONE_SWORD );
	}

	public SkeletonsInGroup() {
		super( GameModifier.DEFAULT, "SkeletonsInGroup", "Skeletons may spawn in groups.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData && spawnedData.level != null ) {
			MOB_GROUPS.spawn( ( PathfinderMob )spawnedData.target );
		}
	}
}

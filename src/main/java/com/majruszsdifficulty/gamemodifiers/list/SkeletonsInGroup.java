package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Items;

public class SkeletonsInGroup extends GameModifier {
	final ItemStackConfig woodenSword = new ItemStackConfig( "SidekickWoodenSword", "Chance for a sidekick to have the Wooden Sword.", ()->Items.WOODEN_SWORD, EquipmentSlot.MAINHAND, 0.5, 0.05, 0.2 );
	final ItemStackConfig stoneSword = new ItemStackConfig( "SidekickStoneSword", "Chance for a sidekick to have the Stone Sword.", ()->Items.STONE_SWORD, EquipmentSlot.MAINHAND, 0.25, 0.05, 0.2 );
	final ItemStackConfig helmet = new ItemStackConfig( "LeaderLeatherHelmet", "Chance for a leader to have the Leather Helmet.", ()->Items.LEATHER_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	final ItemStackConfig chestplate = new ItemStackConfig( "LeaderLeatherChestplate", "Chance for a leader to have the Leather Chestplate.", ()->Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	final ItemStackConfig leggings = new ItemStackConfig( "LeaderLeatherLeggings", "Chance for a leader to have the Leather Leggings.", ()->Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	final ItemStackConfig boots = new ItemStackConfig( "LeaderLeatherBoots", "Chance for a leader to have the Leather Boots.", ()->Items.LEATHER_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	final MobGroupConfig mobGroups = new MobGroupConfig( "Skeletons", ()->EntityType.SKELETON, 1, 3 );

	public SkeletonsInGroup() {
		super( GameModifier.DEFAULT, "SkeletonsInGroup", "Skeletons may spawn in groups." );

		this.mobGroups.addLeaderConfigs( this.helmet, this.chestplate, this.leggings, this.boots );
		this.mobGroups.addSidekickConfigs( this.woodenSword, this.stoneSword );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::spawnGroup );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance( 0.25 ) )
			.addCondition( new CustomConditions.IsNotSidekick() )
			.addCondition( new CustomConditions.IsNotUndeadArmy() )
			.addCondition( new CustomConditions.IsNotTooManyMobsNearby() )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.level != null )
			.addCondition( data->data.target instanceof Skeleton )
			.addConfigs( this.woodenSword, this.stoneSword, this.helmet, this.chestplate, this.leggings, this.boots, this.mobGroups );

		this.addContext( onSpawned );
	}

	private void spawnGroup( OnSpawnedData data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}

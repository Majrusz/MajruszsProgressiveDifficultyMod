package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Items;

public class PiglinsInGroup extends GameModifier {
	static final ItemStackConfig GOLDEN_SWORD = new ItemStackConfig( "SidekickGoldenSword", "Chance for a sidekick to have the Golden Sword.", ()->Items.GOLDEN_SWORD, EquipmentSlot.MAINHAND, 0.4, 0.05, 0.2 );
	static final ItemStackConfig GOLDEN_HELMET = new ItemStackConfig( "LeaderGoldenHelmet", "Chance for a leader to have the Golden Helmet.", ()->Items.GOLDEN_HELMET, EquipmentSlot.HEAD, 0.67, 0.05, 0.75 );
	static final ItemStackConfig GOLDEN_CHESTPLATE = new ItemStackConfig( "LeaderGoldenChestplate", "Chance for a leader to have the Golden Chestplate.", ()->Items.GOLDEN_CHESTPLATE, EquipmentSlot.CHEST, 0.67, 0.05, 0.75 );
	static final ItemStackConfig GOLDEN_LEGGINGS = new ItemStackConfig( "LeaderGoldenLeggings", "Chance for a leader to have the Golden Leggings.", ()->Items.GOLDEN_LEGGINGS, EquipmentSlot.LEGS, 0.67, 0.05, 0.75 );
	static final ItemStackConfig GOLDEN_BOOTS = new ItemStackConfig( "LeaderGoldenBoots", "Chance for a leader to have the Golden Boots.", ()->Items.GOLDEN_BOOTS, EquipmentSlot.FEET, 0.67, 0.05, 0.75 );
	static final MobGroupConfig MOB_GROUPS = new MobGroupConfig( "Piglins", ()->EntityType.PIGLIN, 1, 3 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.25 ) );
		ON_SPAWNED.addCondition( new CustomConditions.IsNotSidekick() );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Piglin ) );
		ON_SPAWNED.addConfigs( GOLDEN_SWORD, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, MOB_GROUPS );

		MOB_GROUPS.addLeaderConfigs( GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS );
		MOB_GROUPS.addSidekickConfigs( GOLDEN_SWORD );
	}

	public PiglinsInGroup() {
		super( GameModifier.DEFAULT, "PiglinsInGroup", "Piglins may spawn in groups.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData && spawnedData.level != null ) {
			MOB_GROUPS.spawn( ( PathfinderMob )spawnedData.target );
		}
	}
}

package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.goals.FollowGroupLeaderGoal;
import com.majruszs_difficulty.goals.TargetAsLeaderGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public abstract class EnemyGroup {
	protected final boolean spawningGroupFailed;
	protected CreatureEntity leader;

	abstract protected CreatureEntity spawnChild( ServerWorld world );

	public EnemyGroup( CreatureEntity leader, ServerWorld world, int minimumAmountOfChildren, int maximumAmountOfChildren ) {
		this.leader = leader;

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( leader, world );
		if( MajruszsDifficulty.RANDOM.nextDouble() >= Config.getChance( Config.Chances.ENEMY_GROUPS ) * clampedRegionalDifficulty ) {
			this.spawningGroupFailed = true;
			return;
		}
		this.spawningGroupFailed = false;

		int childrenAmount = minimumAmountOfChildren + MajruszsDifficulty.RANDOM.nextInt( maximumAmountOfChildren - minimumAmountOfChildren + 1 );
		spawnChildren( childrenAmount, world, this.leader.getPositionVec() );
	}

	protected void giveArmorToLeader( ServerWorld world, Item[] leaderArmor ) {
		if( this.spawningGroupFailed || this.leader == null )
			return;

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( this.leader, world );

		List< ItemStack > itemStacks = new ArrayList<>();
		for( Item item : leaderArmor )
			itemStacks.add( MajruszsHelper.damageAndEnchantItem( new ItemStack( item ), clampedRegionalDifficulty ) );

		this.leader.setItemStackToSlot( EquipmentSlotType.FEET, itemStacks.get( 0 ) );
		this.leader.setItemStackToSlot( EquipmentSlotType.LEGS, itemStacks.get( 1 ) );
		this.leader.setItemStackToSlot( EquipmentSlotType.CHEST, itemStacks.get( 2 ) );
		this.leader.setItemStackToSlot( EquipmentSlotType.HEAD, itemStacks.get( 3 ) );
	}

	protected void giveWeaponTo( LivingEntity entity, ServerWorld world ) {
		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( entity, world );

		ItemStack weapon = generateWeapon();
		if( weapon != null )
			entity.setItemStackToSlot( EquipmentSlotType.MAINHAND, MajruszsHelper.damageAndEnchantItem( weapon, clampedRegionalDifficulty ) );
	}

	protected void setupGoals( CreatureEntity follower, int goalPriority, int targetPriority ) {
		if( follower == null )
			return;

		follower.goalSelector.addGoal( goalPriority, new FollowGroupLeaderGoal( follower, this.leader, 1.0D, 6.0f, 5.0f ) );
		follower.targetSelector.addGoal( targetPriority, new TargetAsLeaderGoal( follower, this.leader ) );
	}

	protected ItemStack generateWeapon() {
		return null;
	}

	private void spawnChildren( int amount, ServerWorld world, Vector3d spawnPosition ) {
		for( int childID = 0; childID < amount; childID++ ) {
			LivingEntity child = spawnChild( world );
			double x = spawnPosition.x - 3 + MajruszsDifficulty.RANDOM.nextInt( 7 );
			double y = spawnPosition.y + 0.5;
			double z = spawnPosition.z - 3 + MajruszsDifficulty.RANDOM.nextInt( 7 );
			child.setPosition( x, y, z );

			world.summonEntity( child );
		}
	}

	protected static class Armors {
		public static Item[] leather = new Item[]{ Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET };
		public static Item[] iron = new Item[]{ Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET };
		public static Item[] golden = new Item[]{ Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET };
	}
}

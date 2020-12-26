package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.goals.FollowGroupLeaderGoal;
import com.majruszs_difficulty.goals.TargetAsLeaderGoal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public abstract class EnemyGroup {
	protected static final double spawnChance = 0.2D;
	private boolean spawningGroupFailed = false;
	protected MonsterEntity leader;
	protected Item[] leaderArmor;
	protected static final Item[] leatherArmor = new Item[]{ Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET };
	protected static final Item[] ironArmor = new Item[]{ Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET };
	protected static final Item[] goldenArmor = new Item[]{ Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET };

	abstract protected LivingEntity spawnChild( ServerWorld world );

	public EnemyGroup( MonsterEntity leader, ServerWorld world, int minimumAmountOfChildren, int maximumAmountOfChildren ) {
		this.leader = leader;
		this.leader.setChild( false );

		if( MajruszsDifficulty.RANDOM.nextDouble() >= spawnChance ) {
			this.spawningGroupFailed = true;
			return;
		}

		Vector3d spawnPosition = this.leader.getPositionVec();

		int childrenAmount = minimumAmountOfChildren + MajruszsDifficulty.RANDOM.nextInt( maximumAmountOfChildren - minimumAmountOfChildren + 1 );
		for( int childID = 0; childID < childrenAmount; childID++ ) {
			LivingEntity child = spawnChild( world );
			child.setPosition( spawnPosition.x - 3 + MajruszsDifficulty.RANDOM.nextInt( 7 ), spawnPosition.y,
				spawnPosition.z - 3 + MajruszsDifficulty.RANDOM.nextInt( 7 )
			);

			world.summonEntity( child );
		}
	}

	protected void giveArmorToLeader( MonsterEntity leader, ServerWorld world, Item[] armor ) {
		if( this.spawningGroupFailed )
			return;

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( leader, world );

		List< ItemStack > itemStacks = new ArrayList<>();
		for( Item item : armor )
			itemStacks.add( tryEnchantArmor( damageItem( new ItemStack( item ) ), clampedRegionalDifficulty ) );

		leader.setItemStackToSlot( EquipmentSlotType.FEET, itemStacks.get( 0 ) );
		leader.setItemStackToSlot( EquipmentSlotType.LEGS, itemStacks.get( 1 ) );
		leader.setItemStackToSlot( EquipmentSlotType.CHEST, itemStacks.get( 2 ) );
		leader.setItemStackToSlot( EquipmentSlotType.HEAD, itemStacks.get( 3 ) );
	}

	protected static void setupGoals( MonsterEntity follower, MonsterEntity leader, int goalPriority, int targetPriority ) {
		follower.goalSelector.addGoal( goalPriority, new FollowGroupLeaderGoal( follower, leader, 1.0D, 6.0f, 5.0f ) );
		follower.targetSelector.addGoal( targetPriority, new TargetAsLeaderGoal( follower, leader ) );
	}

	protected static ItemStack damageItem( ItemStack itemStack ) {
		itemStack.setDamage( MajruszsDifficulty.RANDOM.nextInt( itemStack.getMaxDamage()/2 ) );

		return itemStack;
	}

	protected static ItemStack tryEnchantWeapon( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= 0.25D * clampedRegionalDifficulty )
			return itemStack;

		return enchantItem( itemStack, clampedRegionalDifficulty );
	}

	protected static ItemStack tryEnchantArmor( ItemStack itemStack, double clampedRegionalDifficulty ) {
		if( MajruszsDifficulty.RANDOM.nextDouble() >= 0.5D * clampedRegionalDifficulty )
			return itemStack;

		return enchantItem( itemStack, clampedRegionalDifficulty );
	}

	protected static ItemStack enchantItem( ItemStack itemStack, double clampedRegionalDifficulty ) {
		return EnchantmentHelper.addRandomEnchantment( MajruszsDifficulty.RANDOM, itemStack, ( int )( 5 + 18 * clampedRegionalDifficulty ), false );
	}
}

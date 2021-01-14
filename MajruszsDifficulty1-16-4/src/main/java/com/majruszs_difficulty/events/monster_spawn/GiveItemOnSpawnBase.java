package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

/** Class for giving an item for entity on spawn. */
public abstract class GiveItemOnSpawnBase extends OnEnemyToBeSpawnedBase {
	private final ItemStack itemStackToGive;
	private final EquipmentSlotType equipmentSlotType;
	private final boolean shouldBeEnchanted;
	private final boolean shouldBeDamaged;

	public GiveItemOnSpawnBase( GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD, ItemStack itemStackToGive,
		EquipmentSlotType equipmentSlotType, boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		super( minimumState, shouldChanceBeMultipliedByCRD );
		this.itemStackToGive = itemStackToGive;
		this.equipmentSlotType = equipmentSlotType;
		this.shouldBeEnchanted = shouldBeEnchanted;
		this.shouldBeDamaged = shouldBeDamaged;
	}

	public GiveItemOnSpawnBase( GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD, Item item, EquipmentSlotType equipmentSlotType,
		boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		this( minimumState, shouldChanceBeMultipliedByCRD, new ItemStack( item ), equipmentSlotType, shouldBeEnchanted, shouldBeDamaged );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( entity, world );

		entity.setItemStackToSlot( this.equipmentSlotType, getFinalItemStack( clampedRegionalDifficulty ) );
	}

	/** Returns final item stack with optional enchantments and damaged. */
	private ItemStack getFinalItemStack( double clampedRegionalDifficulty ) {
		if( this.shouldBeEnchanted ) {
			if( this.shouldBeDamaged )
				return MajruszsHelper.damageAndEnchantItem( this.itemStackToGive, clampedRegionalDifficulty );
			else
				return MajruszsHelper.enchantItem( this.itemStackToGive, clampedRegionalDifficulty );
		}

		return this.shouldBeDamaged ? MajruszsHelper.damageItem( this.itemStackToGive ) : this.itemStackToGive;
	}
}

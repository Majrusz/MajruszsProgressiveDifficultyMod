package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.mlib.WorldHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

/** Class for giving an item for entity on spawn. */
public abstract class GiveItemOnSpawnBase extends OnEnemyToBeSpawnedBase {
	protected final EquipmentSlotType equipmentSlotType;
	protected final boolean shouldBeEnchanted;
	protected final boolean shouldBeDamaged;

	public GiveItemOnSpawnBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD, EquipmentSlotType equipmentSlotType, boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
		this.equipmentSlotType = equipmentSlotType;
		this.shouldBeEnchanted = shouldBeEnchanted;
		this.shouldBeDamaged = shouldBeDamaged;
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		double clampedRegionalDifficulty = WorldHelper.getClampedRegionalDifficulty( entity );

		entity.setItemStackToSlot( this.equipmentSlotType, getFinalItemStack( clampedRegionalDifficulty ) );
	}

	/** Returns item stack to give to the entity. */
	public abstract ItemStack getItemStack();

	/** Returns final item stack with optional enchantments and damaged. */
	protected ItemStack getFinalItemStack( double clampedRegionalDifficulty ) {
		if( this.shouldBeEnchanted ) {
			if( this.shouldBeDamaged )
				return ItemHelper.damageAndEnchantItem( getItemStack(), clampedRegionalDifficulty, true, 0.5 );
			else
				return ItemHelper.enchantItem( getItemStack(), clampedRegionalDifficulty, true );
		}

		return this.shouldBeDamaged ? ItemHelper.damageItem( getItemStack(), 0.5 ) : getItemStack();
	}
}

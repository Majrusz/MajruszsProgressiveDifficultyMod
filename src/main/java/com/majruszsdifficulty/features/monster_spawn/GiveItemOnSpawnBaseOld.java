package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameStage;
import com.mlib.items.ItemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Class for giving an item for entity on spawn. */
public abstract class GiveItemOnSpawnBaseOld extends OnEnemyToBeSpawnedBaseOld {
	protected final EquipmentSlot equipmentSlotType;
	protected final boolean shouldBeEnchanted;
	protected final boolean shouldBeDamaged;

	public GiveItemOnSpawnBaseOld( String configName, String configComment, double defaultChance, GameStage.Stage minimumStage,
		boolean shouldChanceBeMultipliedByCRD, EquipmentSlot equipmentSlotType, boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		super( configName, configComment, defaultChance, minimumStage, shouldChanceBeMultipliedByCRD );
		this.equipmentSlotType = equipmentSlotType;
		this.shouldBeEnchanted = shouldBeEnchanted;
		this.shouldBeDamaged = shouldBeDamaged;
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( LivingEntity entity, ServerLevel world ) {
		double clampedRegionalDifficulty = GameStage.getRegionalDifficulty( entity );

		entity.setItemSlot( this.equipmentSlotType, getFinalItemStack( clampedRegionalDifficulty ) );
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
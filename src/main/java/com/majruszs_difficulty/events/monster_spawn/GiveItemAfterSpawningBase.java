package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Class for giving an item for entity after entity was spawned. */
@Mod.EventBusSubscriber
public abstract class GiveItemAfterSpawningBase extends GiveItemOnSpawnBase {
	private static final List< Data > dataList = new ArrayList<>();

	public GiveItemAfterSpawningBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD, EquipmentSlotType equipmentSlotType, boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD, equipmentSlotType, shouldBeEnchanted,
			shouldBeDamaged
		);
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		double clampedRegionalDifficulty = GameState.getRegionalDifficulty( entity );

		Data data = new Data();
		data.uuid = entity.getUniqueID();
		data.itemStack = getFinalItemStack( clampedRegionalDifficulty );
		data.equipmentSlotType = this.equipmentSlotType;

		dataList.add( data );
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( dataList.size() <= 0 || !( event.world instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.world;
		if( world.getDimensionKey() != World.THE_NETHER )
			return;

		for( Data data : dataList ) {
			Entity entity = world.getEntityByUuid( data.uuid );
			if( entity == null )
				continue;

			entity.setItemStackToSlot( data.equipmentSlotType, data.itemStack );
		}

		dataList.clear();
	}

	private static class Data {
		public UUID uuid;
		public ItemStack itemStack;
		public EquipmentSlotType equipmentSlotType;
	}
}

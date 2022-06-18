package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

	public GiveItemAfterSpawningBase( String configName, String configComment, double defaultChance, GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD,
		EquipmentSlot equipmentSlotType, boolean shouldBeEnchanted, boolean shouldBeDamaged
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD, equipmentSlotType, shouldBeEnchanted, shouldBeDamaged );
	}

	/** Called when all requirements were met. */
	@Override
	public void onExecute( LivingEntity entity, ServerLevel world ) {
		double clampedRegionalDifficulty = GameState.getRegionalDifficulty( entity );

		Data data = new Data();
		data.uuid = entity.getUUID();
		data.itemStack = getFinalItemStack( clampedRegionalDifficulty );
		data.equipmentSlotType = this.equipmentSlotType;

		dataList.add( data );
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( dataList.size() <= 0 || !( event.world instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )event.world;
		for( Data data : dataList ) {
			Entity entity = world.getEntity( data.uuid );
			if( entity == null )
				continue;

			entity.setItemSlot( data.equipmentSlotType, data.itemStack );
		}

		dataList.clear();
	}

	private static class Data {
		public UUID uuid;
		public ItemStack itemStack;
		public EquipmentSlot equipmentSlotType;
	}
}

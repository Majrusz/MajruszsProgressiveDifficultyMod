package com.majruszs_difficulty;

import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.items.EndShardLocatorItem;
import com.majruszs_difficulty.renderers.EliteSkeletonRenderer;
import com.majruszs_difficulty.renderers.GiantRenderer;
import com.majruszs_difficulty.renderers.PillagerWolfRenderer;
import com.majruszs_difficulty.renderers.SkyKeeperRenderer;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/** Class registering things only on the client side. */
@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler( GiantEntity.type, GiantRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( PillagerWolfEntity.type, PillagerWolfRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( EliteSkeletonEntity.type, EliteSkeletonRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( SkyKeeperEntity.type, SkyKeeperRenderer::new );

		ItemModelsProperties.registerProperty( Instances.END_SHARD_LOCATOR_ITEM, new ResourceLocation( "shard_distance" ),
			EndShardLocatorItem::calculateDistanceToEndShard
		);
	}
}

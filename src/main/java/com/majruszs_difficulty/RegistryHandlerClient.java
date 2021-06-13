package com.majruszs_difficulty;

import com.majruszs_difficulty.entities.*;
import com.majruszs_difficulty.items.EndShardLocatorItem;
import com.majruszs_difficulty.renderers.*;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/** Class registering things only on the client side. */
@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static final RenderMaterial OCEAN_SHIELD_MATERIAL = new RenderMaterial( AtlasTexture.LOCATION_BLOCKS_TEXTURE, MajruszsDifficulty.getLocation( "entity/ocean_shield" ) );

	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler( GiantEntity.type, GiantRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( PillagerWolfEntity.type, PillagerWolfRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( EliteSkeletonEntity.type, EliteSkeletonRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( SkyKeeperEntity.type, SkyKeeperRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( CreeperlingEntity.type, CreeperlingRenderer::new );

		ItemModelsProperties.registerProperty( Instances.END_SHARD_LOCATOR_ITEM, new ResourceLocation( "shard_distance" ),
			EndShardLocatorItem::calculateDistanceToEndShard
		);
		ItemModelsProperties.registerProperty( Instances.OCEAN_SHIELD_ITEM, new ResourceLocation( "blocking" ),
			( itemStack, world, entity )->entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0f : 0.0f
		);
	}
}

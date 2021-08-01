package com.majruszs_difficulty;

import com.majruszs_difficulty.entities.*;
import com.majruszs_difficulty.items.EndShardLocatorItem;
import com.majruszs_difficulty.models.CreeperlingModel;
import com.majruszs_difficulty.models.OceanShieldModel;
import com.majruszs_difficulty.renderers.*;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

/** Class registering things only on the client side. */
@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static final Material OCEAN_SHIELD_MATERIAL = new Material( InventoryMenu.BLOCK_ATLAS,
		MajruszsDifficulty.getLocation( "entity/ocean_shield" )
	);

	public static void setup() {
		ForgeHooksClient.registerLayerDefinition( OceanShieldRenderer.LAYER_LOCATION, ()->OceanShieldModel.createBodyLayer( CubeDeformation.NONE ) );
		ForgeHooksClient.registerLayerDefinition( CreeperlingRenderer.LAYER_LOCATION, ()->CreeperlingModel.createBodyLayer( CubeDeformation.NONE ) );

		EntityRenderers.register( GiantEntity.type, GiantRenderer::new );
		EntityRenderers.register( PillagerWolfEntity.type, PillagerWolfRenderer::new );
		EntityRenderers.register( EliteSkeletonEntity.type, EliteSkeletonRenderer::new );
		EntityRenderers.register( SkyKeeperEntity.type, SkyKeeperRenderer::new );
		EntityRenderers.register( CreeperlingEntity.type, CreeperlingRenderer::new );
		EntityRenderers.register( ParasiteEntity.type, ParasiteRenderer::new );

		ItemProperties.register( Instances.END_SHARD_LOCATOR_ITEM, new ResourceLocation( "shard_distance" ),
			EndShardLocatorItem::calculateDistanceToEndShard
		);
		ItemProperties.register( Instances.OCEAN_SHIELD_ITEM, new ResourceLocation( "blocking" ),
			( itemStack, world, entity, p_174668_ )->entity != null && entity.isBlocking() && entity.getUseItem() == itemStack ? 1.0f : 0.0f
		);
	}
}

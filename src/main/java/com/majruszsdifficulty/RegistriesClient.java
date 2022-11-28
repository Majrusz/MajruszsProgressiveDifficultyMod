package com.majruszsdifficulty;

import com.majruszsdifficulty.items.EndShardLocatorItem;
import com.majruszsdifficulty.items.WitherBowItem;
import com.majruszsdifficulty.models.BlackWidowModel;
import com.majruszsdifficulty.models.CreeperlingModel;
import com.majruszsdifficulty.models.SpiderQueenModel;
import com.majruszsdifficulty.models.TankModel;
import com.majruszsdifficulty.renderers.BlackWidowRenderer;
import com.majruszsdifficulty.renderers.CreeperlingRenderer;
import com.majruszsdifficulty.renderers.SpiderQueenRenderer;
import com.majruszsdifficulty.renderers.TankRenderer;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn( Dist.CLIENT )
public class RegistriesClient {
	public static void setup() {
		ForgeHooksClient.registerLayerDefinition( CreeperlingRenderer.LAYER_LOCATION, ()->CreeperlingModel.createBodyLayer( CubeDeformation.NONE ) );
		ForgeHooksClient.registerLayerDefinition( TankRenderer.LAYER_LOCATION, ()->TankModel.createBodyLayer( CubeDeformation.NONE ) );
		ForgeHooksClient.registerLayerDefinition( BlackWidowRenderer.LAYER_LOCATION, BlackWidowModel::createBodyLayer );
		ForgeHooksClient.registerLayerDefinition( SpiderQueenRenderer.LAYER_LOCATION, SpiderQueenModel::createBodyLayer );

		EntityRenderers.register( Registries.CREEPERLING.get(), CreeperlingRenderer::new );
		EntityRenderers.register( Registries.TANK.get(), TankRenderer::new );
		EntityRenderers.register( Registries.BLACK_WIDOW.get(), BlackWidowRenderer::new );
		EntityRenderers.register( Registries.SPIDER_QUEEN.get(), SpiderQueenRenderer::new );

		ItemProperties.register( Registries.ENDERIUM_SHARD_LOCATOR.get(), new ResourceLocation( "shard_distance" ), EndShardLocatorItem::calculateDistanceToEndShard );
		ItemProperties.register( Registries.WITHER_BOW.get(), new ResourceLocation( "pull" ), WitherBowItem::calculatePull );
		ItemProperties.register( Registries.WITHER_BOW.get(), new ResourceLocation( "pulling" ), WitherBowItem::calculatePulling );
	}
}

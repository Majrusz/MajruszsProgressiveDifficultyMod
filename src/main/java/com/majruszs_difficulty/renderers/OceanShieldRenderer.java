package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.RegistryHandlerClient;
import com.majruszs_difficulty.models.OceanShieldModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.List;

@OnlyIn( Dist.CLIENT )
public class OceanShieldRenderer extends BlockEntityWithoutLevelRenderer {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation( MajruszsDifficulty.getLocation( "ocean_shield" ), "main" );
	private final OceanShieldModel oceanShield;

	public OceanShieldRenderer( BlockEntityRenderDispatcher p_172550_, EntityModelSet entityModelSet ) {
		super( p_172550_, entityModelSet );

		this.oceanShield = new OceanShieldModel( entityModelSet.bakeLayer( LAYER_LOCATION ) );
	}

	@Override
	public void renderByItem( ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource,
		int combinedLight, int combinedOverlay
	) {
		poseStack.pushPose();
		poseStack.scale( 1.0F, -1.0F, -1.0F );
		Material material = RegistryHandlerClient.OCEAN_SHIELD_MATERIAL;
		VertexConsumer vertexconsumer = material.sprite()
			.wrap( ItemRenderer.getFoilBufferDirect( bufferSource, this.oceanShield.renderType( material.atlasLocation() ), true,
				itemStack.hasFoil()
			) );
		for( ModelPart modelPart : this.oceanShield.getModels() )
			modelPart.render( poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F );

		poseStack.popPose();
	}
}

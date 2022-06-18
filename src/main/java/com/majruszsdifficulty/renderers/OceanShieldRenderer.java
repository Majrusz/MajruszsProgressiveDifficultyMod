package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.RegistryHandlerClient;
import com.majruszsdifficulty.models.OceanShieldModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class OceanShieldRenderer extends BlockEntityWithoutLevelRenderer {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation( Registries.getLocation( "ocean_shield" ), "main" );
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
		VertexConsumer vertexconsumer = material.sprite().wrap( ItemRenderer.getFoilBufferDirect( bufferSource,
			this.oceanShield.renderType( material.atlasLocation() ),
			true,
			itemStack.hasFoil()
		) );
		for( ModelPart modelPart : this.oceanShield.getModels() )
			modelPart.render( poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F );

		poseStack.popPose();
	}
}

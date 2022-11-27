package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.BlackWidowEntity;
import com.majruszsdifficulty.models.BlackWidowModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BlackWidowRenderer extends MobRenderer< BlackWidowEntity, BlackWidowModel< BlackWidowEntity > > {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation( Registries.getLocation( "black_widow" ), "main" );
	private static final ResourceLocation TEXTURE_LOCATION = Registries.getLocation( "textures/entity/black_widow.png" );

	public BlackWidowRenderer( EntityRendererProvider.Context context ) {
		super( context, new BlackWidowModel<>( context.bakeLayer( LAYER_LOCATION ) ), 0.25f );
		this.addLayer( new BlackWidowEyesLayer<>( this ) );
	}

	@Override
	public ResourceLocation getTextureLocation( BlackWidowEntity blackWidow ) {
		return TEXTURE_LOCATION;
	}

	@Override
	public void render( BlackWidowEntity blackWidow, float p_114209_, float partialTicks, PoseStack poseStack,
		MultiBufferSource bufferSource, int packedLight
	) {
		this.model.prepareMobModel( blackWidow, 0.0F, 0.0F, partialTicks );

		super.render( blackWidow, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}
}

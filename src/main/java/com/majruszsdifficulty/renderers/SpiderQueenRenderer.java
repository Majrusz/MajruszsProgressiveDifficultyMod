package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.SpiderQueenEntity;
import com.majruszsdifficulty.models.SpiderQueenModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class SpiderQueenRenderer extends MobRenderer< SpiderQueenEntity, SpiderQueenModel< SpiderQueenEntity > > {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation( Registries.getLocation( "spider_queen" ), "main" );
	private static final ResourceLocation TEXTURE_LOCATION = Registries.getLocation( "textures/entity/spider_queen.png" );

	public SpiderQueenRenderer( EntityRendererProvider.Context context ) {
		super( context, new SpiderQueenModel<>( context.bakeLayer( LAYER_LOCATION ) ), 1.5f );
	}

	@Override
	public ResourceLocation getTextureLocation( SpiderQueenEntity spiderQueen ) {
		return TEXTURE_LOCATION;
	}

	@Override
	public void render( SpiderQueenEntity spiderQueen, float p_114209_, float partialTicks, PoseStack poseStack,
		MultiBufferSource bufferSource, int packedLight
	) {
		this.model.prepareMobModel( spiderQueen, 0.0f, 0.0f, partialTicks );

		super.render( spiderQueen, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}

	@Override
	protected float getFlipDegrees( SpiderQueenEntity spiderQueen ) {
		return 180.0f;
	}
}

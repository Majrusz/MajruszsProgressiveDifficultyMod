package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class CreeperlingRenderer extends MobRenderer< CreeperlingEntity, CreeperlingModel< CreeperlingEntity > > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "creeperling" );
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/creeperling.png" );

	public CreeperlingRenderer( EntityRendererProvider.Context context ) {
		super( context, new CreeperlingModel<>( context.bakeLayer( LAYER ) ), 0.25f );
	}

	@Override
	public ResourceLocation getTextureLocation( CreeperlingEntity creeperling ) {
		return TEXTURE;
	}

	@Override
	public void render( CreeperlingEntity creeperling, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
		int packedLight
	) {
		this.model.prepareMobModel( creeperling, 0.0f, 0.0f, partialTicks );

		super.render( creeperling, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}

	@Override
	protected float getWhiteOverlayProgress( CreeperlingEntity creeperling, float partialTicks ) {
		float f = creeperling.getSwelling( partialTicks );

		return ( int )( f * 10.0f ) % 2 == 0 ? 0.0f : Mth.clamp( f, 0.5f, 1.0f );
	}

	@Override
	protected void scale( CreeperlingEntity creeperling, PoseStack stack, float partialTicks ) {
		float f = creeperling.getSwelling( partialTicks );
		float f1 = 1.0f + Mth.sin( f * 100.0f ) * f * 0.01f;
		f = Mth.clamp( f, 0.0f, 1.0f );
		f = f * f;
		f = f * f;
		float f2 = ( 1.0f + f * 0.4f ) * f1;
		float f3 = ( 1.0f + f * 0.1f ) / f1;
		stack.scale( f2, f3, f2 );
	}
}

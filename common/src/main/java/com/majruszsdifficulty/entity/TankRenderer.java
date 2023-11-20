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

@OnlyIn( Dist.CLIENT )
public class TankRenderer extends MobRenderer< TankEntity, TankModel< TankEntity > > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "tank" );
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/tank.png" );

	public TankRenderer( EntityRendererProvider.Context context ) {
		super( context, new TankModel<>( context.bakeLayer( LAYER ) ), 0.5f );
	}

	@Override
	public ResourceLocation getTextureLocation( TankEntity tank ) {
		return TEXTURE;
	}

	@Override
	public void render( TankEntity tank, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight ) {
		this.model.prepareMobModel( tank, 0.0f, 0.0f, partialTicks );

		super.render( tank, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}
}

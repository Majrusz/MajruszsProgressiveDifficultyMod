package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.models.TankModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class TankRenderer extends MobRenderer< TankEntity, TankModel< TankEntity > > {
	public static final ModelLayerLocation LAYER = Registries.getModelLayer( "tank" );
	static final ResourceLocation TEXTURE = Registries.getLocation( "textures/entity/tank.png" );

	public TankRenderer( EntityRendererProvider.Context context ) {
		super( context, new TankModel<>( context.bakeLayer( LAYER ) ), 0.5f );
	}

	@Override
	public ResourceLocation getTextureLocation( TankEntity tank ) {
		return TEXTURE;
	}

	@Override
	public void render( TankEntity tank, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
		int packedLight
	) {
		this.model.prepareMobModel( tank, 0.0F, 0.0F, partialTicks );

		super.render( tank, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}
}

package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CerberusEntity;
import com.majruszsdifficulty.models.CerberusModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CerberusRenderer extends MobRenderer< CerberusEntity, CerberusModel< CerberusEntity > > {
	public static final ModelLayerLocation LAYER = Registries.getModelLayer( "cerberus" );
	static final ResourceLocation TEXTURE = Registries.getLocation( "textures/entity/cerberus.png" );

	public CerberusRenderer( EntityRendererProvider.Context context ) {
		super( context, new CerberusModel<>( context.bakeLayer( LAYER ) ), 1.0f );

		this.addLayer( new CerberusEyesLayer( this ) );
	}

	@Override
	public ResourceLocation getTextureLocation( CerberusEntity cerberus ) {
		return TEXTURE;
	}

	@Override
	public void render( CerberusEntity cerberus, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
		int packedLight
	) {
		this.model.prepareMobModel( cerberus, 0.0F, 0.0F, partialTicks );

		super.render( cerberus, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}
}

package com.majruszsdifficulty.entity;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn( Dist.CLIENT )
public class CerberusRenderer extends MobRenderer< Cerberus, CerberusModel< Cerberus > > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cerberus" );
	public static final ModelLayerLocation ARMOR_LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cerberus", "armor" );
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/cerberus.png" );

	public CerberusRenderer( EntityRendererProvider.Context context ) {
		super( context, new CerberusModel<>( context.bakeLayer( LAYER ) ), 0.75f );

		this.addLayer( new CerberusEyesLayer( this ) );
		this.addLayer( new UndeadArmyArmorLayer<>( this, new CerberusModel<>( context.bakeLayer( ARMOR_LAYER ) ), "textures/entity/cerberus_undead_army_armor.png" ) );
	}

	@Override
	public ResourceLocation getTextureLocation( Cerberus cerberus ) {
		return TEXTURE;
	}

	@Override
	public void render( Cerberus cerberus, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight ) {
		this.model.prepareMobModel( cerberus, 0.0f, 0.0f, partialTicks );

		super.render( cerberus, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}

	@OnlyIn( Dist.CLIENT )
	public static class CerberusEyesLayer extends EyesLayer< Cerberus, CerberusModel< Cerberus > > {
		static final RenderType EYES = RenderType.eyes( MajruszsDifficulty.HELPER.getLocation( "textures/entity/cerberus_eyes.png" ) );

		public CerberusEyesLayer( RenderLayerParent< Cerberus, CerberusModel< Cerberus > > layer ) {
			super( layer );
		}

		@Override
		public RenderType renderType() {
			return EYES;
		}
	}
}

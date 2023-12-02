package com.majruszsdifficulty.entity;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// copied from net.minecraft.client.renderer.entity.IllusionerRenderer
@OnlyIn( Dist.CLIENT )
public class IllusionerRenderer extends IllagerRenderer< Illusioner > {
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/illusioner.png" );

	public IllusionerRenderer( EntityRendererProvider.Context $$0 ) {
		super( $$0, new IllagerModel<>( $$0.bakeLayer( ModelLayers.ILLUSIONER ) ), 0.5f );

		this.addLayer( new ItemInHandLayer<>( this, $$0.getItemInHandRenderer() ) {
			public void render( PoseStack $$0, MultiBufferSource $$1, int $$2, Illusioner $$3, float $$4, float $$5, float $$6, float $$7, float $$8,
				float $$9
			) {
				if( $$3.isCastingSpell() || $$3.isAggressive() ) {
					super.render( $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9 );
				}

			}
		} );
		( ( IllagerModel )this.model ).getHat().visible = true;
	}

	public ResourceLocation getTextureLocation( Illusioner $$0 ) {
		return TEXTURE;
	}

	public void render( Illusioner $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5 ) {
		if( $$0.isInvisible() ) {
			Vec3[] $$6 = $$0.getIllusionOffsets( $$2 );
			float $$7 = this.getBob( $$0, $$2 );

			for( int $$8 = 0; $$8 < $$6.length; ++$$8 ) {
				$$3.pushPose();
				$$3.translate( $$6[ $$8 ].x + ( double )Mth.cos( ( float )$$8 + $$7 * 0.5F ) * 0.025D, $$6[ $$8 ].y + ( double )Mth.cos( ( float )$$8 + $$7 * 0.75F ) * 0.0125D, $$6[ $$8 ].z + ( double )Mth.cos( ( float )$$8 + $$7 * 0.7F ) * 0.025D );
				super.render( $$0, $$1, $$2, $$3, $$4, $$5 );
				$$3.popPose();
			}
		} else {
			super.render( $$0, $$1, $$2, $$3, $$4, $$5 );
		}

	}

	protected boolean isBodyVisible( Illusioner $$0 ) {
		return true;
	}
}

package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.entities.CreeperlingEntity;
import com.majruszs_difficulty.models.CreeperlingModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for new Creeperling entity. */
@OnlyIn( Dist.CLIENT )
public class CreeperlingRenderer extends MobRenderer< CreeperlingEntity, CreeperlingModel< CreeperlingEntity > > {
	private static final ResourceLocation CREEPERLING_TEXTURES = MajruszsDifficulty.getLocation( "textures/entity/creeperling.png" );

	public CreeperlingRenderer( EntityRendererManager renderManager ) {
		super( renderManager, new CreeperlingModel<>(), 0.25f );
	}

	protected float getOverlayProgress( CreeperlingEntity creeperling, float partialTicks ) {
		float f = creeperling.getCreeperFlashIntensity( partialTicks );
		return ( int )( f * 10.0f ) % 2 == 0 ? 0.0f : MathHelper.clamp( f, 0.5f, 1.0f );
	}

	protected void preRenderCallback( CreeperlingEntity creeperling, MatrixStack matrixStack, float partialTickTime ) {
		float f = creeperling.getCreeperFlashIntensity( partialTickTime );
		float f1 = 1.0f + MathHelper.sin( f * 100.0f ) * f * 0.01f;
		f = MathHelper.clamp( f, 0.0f, 1.0f );
		f = f * f;
		f = f * f;
		float f2 = ( 1.0f + f * 0.4f ) * f1;
		float f3 = ( 1.0f + f * 0.1f ) / f1;
		matrixStack.scale( f2, f3, f2 );
	}

	/**
	 Returns the location of an entity's texture.
	 */
	public ResourceLocation getEntityTexture( CreeperlingEntity entity ) {
		return CREEPERLING_TEXTURES;
	}
}

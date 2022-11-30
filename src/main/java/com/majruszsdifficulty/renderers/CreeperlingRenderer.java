package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.models.CreeperlingModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CreeperlingRenderer extends MobRenderer< CreeperlingEntity, CreeperlingModel< CreeperlingEntity > > {
	public static final ModelLayerLocation LAYER = Registries.getModelLayer( "creeperling" );
	static final ResourceLocation TEXTURE = Registries.getLocation( "textures/entity/creeperling.png" );

	public CreeperlingRenderer( EntityRendererProvider.Context context ) {
		super( context, new CreeperlingModel<>( context.bakeLayer( LAYER ) ), 0.25f );
	}

	@Override
	protected float getWhiteOverlayProgress( CreeperlingEntity creeperling, float partialTicks ) {
		float f = creeperling.getSwelling( partialTicks );
		return ( int )( f * 10.0f ) % 2 == 0 ? 0.0f : Mth.clamp( f, 0.5f, 1.0f );
	}

	@Override
	protected void scale( CreeperlingEntity creeperling, PoseStack stack, float partialTickTime ) {
		float f = creeperling.getSwelling( partialTickTime );
		float f1 = 1.0f + Mth.sin( f * 100.0f ) * f * 0.01f;
		f = Mth.clamp( f, 0.0f, 1.0f );
		f = f * f;
		f = f * f;
		float f2 = ( 1.0f + f * 0.4f ) * f1;
		float f3 = ( 1.0f + f * 0.1f ) / f1;
		stack.scale( f2, f3, f2 );
	}

	public ResourceLocation getTextureLocation( CreeperlingEntity entity ) {
		return TEXTURE;
	}
}

package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Sky Keeper entity. */
@OnlyIn( Dist.CLIENT )
public class SkyKeeperRenderer extends PhantomRenderer {
	private static final ResourceLocation SKY_KEEPER_TEXTURE = MajruszsHelper.getResource( "textures/entity/sky_keeper.png" );

	public SkyKeeperRenderer( EntityRendererManager renderManagerIn ) {
		super( renderManagerIn );
		this.layerRenderers.clear();
		this.layerRenderers.add( new SkyKeeperEyesLayer<>( this ) );
	}

	public ResourceLocation getEntityTexture( PhantomEntity entity ) {
		return SKY_KEEPER_TEXTURE;
	}
}
package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Sky Keeper entity. */
@OnlyIn( Dist.CLIENT )
public class SkyKeeperRenderer extends PhantomRenderer {
	private static final ResourceLocation SKY_KEEPER_TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/sky_keeper.png" );

	public SkyKeeperRenderer( EntityRendererProvider.Context context ) {
		super( context );
		overwriteLayers();
	}

	public ResourceLocation getTextureLocation( Phantom entity ) {
		return SKY_KEEPER_TEXTURE;
	}

	/** Overwrites standard Phantom layers with Sky Keeper's one. */
	protected void overwriteLayers() {
		this.layers.clear();
		this.layers.add( new SkyKeeperEyesLayer<>( this ) );
	}
}
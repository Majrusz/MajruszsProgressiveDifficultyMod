package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Layer responsible for drawing Sky Keeper's eyes separately. */
@OnlyIn( Dist.CLIENT )
public class SkyKeeperEyesLayer< T extends Entity > extends AbstractEyesLayer< T, PhantomModel< T > > {
	private static final RenderType RENDER_TYPE = RenderType.getEyes( MajruszsDifficulty.getLocation( "textures/entity/sky_keeper_eyes.png" ) );

	public SkyKeeperEyesLayer( IEntityRenderer< T, PhantomModel< T > > renderer ) {
		super( renderer );
	}

	public RenderType getRenderType() {
		return RENDER_TYPE;
	}
}

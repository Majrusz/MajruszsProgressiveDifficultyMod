package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Layer responsible for drawing Sky Keeper's eyes separately. */
@OnlyIn( Dist.CLIENT )
public class SkyKeeperEyesLayer< T extends Phantom > extends EyesLayer< T, PhantomModel< T > > {
	private static final RenderType RENDER_TYPE = RenderType.eyes( MajruszsDifficulty.getLocation( "textures/entity/sky_keeper_eyes.png" ) );

	public SkyKeeperEyesLayer( RenderLayerParent< T, PhantomModel< T > > renderer ) {
		super( renderer );
	}

	@Override
	public RenderType renderType() {
		return RENDER_TYPE;
	}
}

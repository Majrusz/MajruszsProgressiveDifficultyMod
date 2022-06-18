package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Elite Skeleton entity. */
@OnlyIn( Dist.CLIENT )
public class EliteSkeletonRenderer extends SkeletonRenderer {
	private static final ResourceLocation TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/elite_skeleton.png" );

	public EliteSkeletonRenderer( EntityRendererProvider.Context context ) {
		super( context );
	}

	@Override
	public ResourceLocation getTextureLocation( AbstractSkeleton entity ) {
		return TEXTURE;
	}
}
package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Elite Skeleton entity. */
@OnlyIn( Dist.CLIENT )
public class EliteSkeletonRenderer extends SkeletonRenderer {
	private static final ResourceLocation TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/elite_skeleton.png" );

	public EliteSkeletonRenderer( EntityRendererManager renderManager ) {
		super( renderManager );
	}

	@Override
	public ResourceLocation getTextureLocation( AbstractSkeletonEntity entity ) {
		return TEXTURE;
	}
}
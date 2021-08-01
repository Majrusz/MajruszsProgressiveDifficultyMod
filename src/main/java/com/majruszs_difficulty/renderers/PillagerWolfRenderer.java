package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Pillager's Wolf entity. */
@OnlyIn( Dist.CLIENT )
public class PillagerWolfRenderer extends WolfRenderer {
	private static final ResourceLocation ANGRY_WOLF_TEXTURES = MajruszsDifficulty.getLocation( "textures/entity/pillager_wolf.png" );

	public PillagerWolfRenderer( EntityRendererProvider.Context context ) {
		super( context );
	}

	@Override
	public ResourceLocation getTextureLocation( Wolf entity ) {
		return ANGRY_WOLF_TEXTURES;
	}
}

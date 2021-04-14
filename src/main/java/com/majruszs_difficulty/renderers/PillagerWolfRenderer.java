package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Pillager's Wolf entity. */
@OnlyIn( Dist.CLIENT )
public class PillagerWolfRenderer extends WolfRenderer {
	private static final ResourceLocation ANGRY_WOLF_TEXTURES = MajruszsDifficulty.getLocation( "textures/entity/pillager_wolf.png" );

	public PillagerWolfRenderer( EntityRendererManager rendererManager ) {
		super( rendererManager );
	}

	@Override
	public ResourceLocation getTextureLocation( WolfEntity entity ) {
		return ANGRY_WOLF_TEXTURES;
	}
}

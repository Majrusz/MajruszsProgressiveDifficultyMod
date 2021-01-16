package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.entities.GiantEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Giant entity. */
@OnlyIn( Dist.CLIENT )
public class GiantRenderer extends ZombieRenderer {
	private static final ResourceLocation TEXTURE = MajruszsHelper.getResource( "textures/entity/giant.png" );

	public GiantRenderer( EntityRendererManager renderManagerIn ) {
		super( renderManagerIn );
		this.shadowSize = 0.375f * GiantEntity.scale;
	}

	@Override
	protected void preRenderCallback( ZombieEntity entity, MatrixStack matrixStackIn, float partialTickTime ) {
		matrixStackIn.scale( GiantEntity.scale, GiantEntity.scale, GiantEntity.scale );
	}

	@Override
	public ResourceLocation getEntityTexture( ZombieEntity entity ) {
		return TEXTURE;
	}
}
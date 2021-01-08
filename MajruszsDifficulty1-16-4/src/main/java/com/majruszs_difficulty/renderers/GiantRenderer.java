package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.entities.GiantEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Giant entity. */
@OnlyIn( Dist.CLIENT )
public class GiantRenderer extends ZombieRenderer {
	public GiantRenderer( EntityRendererManager renderManagerIn ) {
		super( renderManagerIn );
		this.shadowSize = 0.375f * GiantEntity.scale;
	}

	@Override
	protected void preRenderCallback( ZombieEntity entity, MatrixStack matrixStackIn, float partialTickTime ) {
		matrixStackIn.scale( GiantEntity.scale, GiantEntity.scale, GiantEntity.scale );
	}
}
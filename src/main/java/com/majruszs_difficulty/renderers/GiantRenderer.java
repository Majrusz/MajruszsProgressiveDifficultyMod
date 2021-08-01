package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.entities.GiantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Renderer for Giant entity. */
@OnlyIn( Dist.CLIENT )
public class GiantRenderer extends ZombieRenderer {
	private static final ResourceLocation TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/giant.png" );

	public GiantRenderer( EntityRendererProvider.Context context ) {
		super( context );
		this.shadowRadius = 0.375f * GiantEntity.scale;
	}

	@Override
	protected void scale( Zombie entity, PoseStack stack, float partialTickTime ) {
		stack.scale( GiantEntity.scale, GiantEntity.scale, GiantEntity.scale );
	}

	@Override
	public ResourceLocation getTextureLocation( Zombie entity ) {
		return TEXTURE;
	}
}
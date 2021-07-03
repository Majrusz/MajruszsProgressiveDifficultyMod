package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class ParasiteRenderer< EntityType extends SpiderEntity > extends MobRenderer< EntityType, SpiderModel< EntityType > > {
	private static final ResourceLocation TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/parasite.png" );
	private static final RenderType RENDER_TYPE = RenderType.getEyes( MajruszsDifficulty.getLocation( "textures/entity/parasite_eyes.png" ) );
	private static final float PARASITE_SCALE = 0.7f;

	public ParasiteRenderer( EntityRendererManager rendererManager ) {
		super( rendererManager, new SpiderModel<>(), 0.8f * PARASITE_SCALE );
		this.addLayer( new ParasiteEyesLayer<>( this ) );
	}

	@Override
	protected float getDeathMaxRotation( EntityType entity ) {
		return 180.0f;
	}

	@Override
	protected void preRenderCallback( SpiderEntity entity, MatrixStack matrixStack, float tickTime ) {
		matrixStack.scale( PARASITE_SCALE, PARASITE_SCALE, PARASITE_SCALE );
	}

	@Override
	public ResourceLocation getEntityTexture( EntityType entity ) {
		return TEXTURE;
	}

	private class ParasiteEyesLayer< ModelType extends SpiderModel< EntityType > > extends AbstractEyesLayer< EntityType, ModelType > {
		public ParasiteEyesLayer( IEntityRenderer< EntityType, ModelType > renderer ) {
			super( renderer );
		}

		@Override
		public RenderType getRenderType() {
			return RENDER_TYPE;
		}
	}
}


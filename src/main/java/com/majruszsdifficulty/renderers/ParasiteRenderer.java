package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class ParasiteRenderer< EntityType extends Spider > extends MobRenderer< EntityType, SpiderModel< EntityType > > {
	private static final ResourceLocation TEXTURE = MajruszsDifficulty.getLocation( "textures/entity/parasite.png" );
	private static final RenderType RENDER_TYPE = RenderType.eyes( MajruszsDifficulty.getLocation( "textures/entity/parasite_eyes.png" ) );
	private static final float PARASITE_SCALE = 0.7f;

	public ParasiteRenderer( EntityRendererProvider.Context context ) {
		super( context, new SpiderModel<>( context.bakeLayer( ModelLayers.SPIDER ) ), 0.8f * PARASITE_SCALE );
		this.addLayer( new ParasiteEyesLayer<>( this ) );
	}

	@Override
	protected float getFlipDegrees( EntityType entity ) {
		return 180.0f;
	}

	@Override
	protected void scale( Spider entity, PoseStack stack, float tickTime ) {
		stack.scale( PARASITE_SCALE, PARASITE_SCALE, PARASITE_SCALE );
	}

	@Override
	public ResourceLocation getTextureLocation( EntityType entity ) {
		return TEXTURE;
	}

	private class ParasiteEyesLayer< EntityType extends Spider, ModelType extends SpiderModel< EntityType > >
		extends EyesLayer< EntityType, ModelType > {
		public ParasiteEyesLayer( RenderLayerParent< EntityType, ModelType > renderer ) {
			super( renderer );
		}

		@Override
		public RenderType renderType() {
			return RENDER_TYPE;
		}
	}
}


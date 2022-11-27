package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.models.BlackWidowModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BlackWidowEyesLayer< EntityType extends Entity, ModelType extends BlackWidowModel< EntityType > >
	extends EyesLayer< EntityType, ModelType > {
	private static final RenderType SPIDER_EYES = RenderType.eyes( Registries.getLocation( "textures/entity/black_widow_eyes.png" ) );

	public BlackWidowEyesLayer( RenderLayerParent< EntityType, ModelType > layer ) {
		super( layer );
	}

	@Override
	public RenderType renderType() {
		return SPIDER_EYES;
	}
}
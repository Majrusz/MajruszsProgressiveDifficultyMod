package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.BlackWidowEntity;
import com.majruszsdifficulty.models.BlackWidowModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BlackWidowEyesLayer extends EyesLayer< BlackWidowEntity, BlackWidowModel< BlackWidowEntity > > {
	static final RenderType SPIDER_EYES = Registries.getEyesRenderType( "textures/entity/black_widow_eyes.png" );

	public BlackWidowEyesLayer( RenderLayerParent< BlackWidowEntity, BlackWidowModel< BlackWidowEntity > > layer ) {
		super( layer );
	}

	@Override
	public RenderType renderType() {
		return SPIDER_EYES;
	}
}
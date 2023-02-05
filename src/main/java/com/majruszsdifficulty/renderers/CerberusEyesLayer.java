package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CerberusEntity;
import com.majruszsdifficulty.models.CerberusModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CerberusEyesLayer extends EyesLayer< CerberusEntity, CerberusModel< CerberusEntity > > {
	static final RenderType EYES = Registries.getEyesRenderType( "textures/entity/cerberus_eyes.png" );

	public CerberusEyesLayer( RenderLayerParent< CerberusEntity, CerberusModel< CerberusEntity > > layer ) {
		super( layer );
	}

	@Override
	public RenderType renderType() {
		return EYES;
	}
}
package com.majruszs_difficulty.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** New armor model only for Hermes boots. (with wings) */
@OnlyIn( Dist.CLIENT )
public class HermesArmorModel extends BipedModel< LivingEntity > {
	public HermesArmorModel() {
		super( 1.0f );

		this.bipedLeftLeg.setTextureOffset( 16, 0 )
			.addBox( 3.0f, 8.0f, 1.0f+0.0f, 1.0f, 3.0f, 2.0f, 0.0f, 0.0f, 0.0f );
		this.bipedLeftLeg.setTextureOffset( 22, 0 )
			.addBox( 3.0f, 8.0f, 1.0f+2.0f, 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f );
		this.bipedLeftLeg.setTextureOffset( 26, 0 )
			.addBox( 3.0f, 8.0f, 1.0f+3.0f, 1.0f, 1.0f, 2.0f, 0.0f, 0.0f, 0.0f );

		this.bipedRightLeg.setTextureOffset( 16, 0 )
			.addBox( -4.0f, 8.0f, 1.0f+0.0f, 1.0f, 3.0f, 2.0f, 0.0f, 0.0f, 0.0f );
		this.bipedRightLeg.setTextureOffset( 22, 0 )
			.addBox( -4.0f, 8.0f, 1.0f+2.0f, 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f );
		this.bipedRightLeg.setTextureOffset( 26, 0 )
			.addBox( -4.0f, 8.0f, 1.0f+3.0f, 1.0f, 1.0f, 2.0f, 0.0f, 0.0f, 0.0f );
	}
}

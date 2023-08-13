package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.GiantEntity;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class GiantModel< Type extends GiantEntity > extends AbstractZombieModel< Type > {
	public GiantModel( ModelPart root ) {
		super( root );
	}

	@Override
	public boolean isAggressive( GiantEntity giant ) {
		return false;
	}

	@Override
	public void setupAnim( Type giant, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		super.setupAnim( giant, 0.25f * limbSwing, 0.5f * limbSwingAmount, ageInTicks, netHeadYaw, headPitch );
	}
}

package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CursedArmorEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CursedArmorModel< Type extends CursedArmorEntity > extends HumanoidModel< Type > {
	public CursedArmorModel( ModelPart root ) {
		super( root, RenderType::entityTranslucent );
	}
}

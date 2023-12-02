package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.modhelper.Resource;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

@OnlyIn( Dist.CLIENT )
public class CursedArmorModel< Type extends CursedArmor > extends HumanoidModel< Type > {
	public static Resource< ModelDef > MODEL = MajruszsDifficulty.HELPER.loadClient( "cursed_armor_model", ModelDef.class );
	public final ModelParts modelParts;

	public CursedArmorModel( ModelPart modelPart ) {
		super( modelPart );

		this.modelParts = new ModelParts( modelPart, MODEL.get() );
	}

	@Override
	public void setupAnim( Type cursedArmor, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.modelParts.reset();

		super.setupAnim( cursedArmor, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch );

		cursedArmor.getAnimations().forEach( animation->animation.apply( this.modelParts, ageInTicks ) );
	}
}


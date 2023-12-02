package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.modhelper.LazyResource;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class CreeperlingModel< Type extends Creeperling > extends HierarchicalModel< Type > {
	public static LazyResource< ModelDef > MODEL = MajruszsDifficulty.HELPER.load( "creeperling_model", ModelDef.class, PackType.CLIENT_RESOURCES );
	public final ModelParts modelParts;
	public final ModelPart head, leftBackFoot, leftFrontFoot, rightBackFoot, rightFrontFoot;

	public CreeperlingModel( ModelPart modelPart ) {
		this.modelParts = new ModelParts( modelPart, MODEL.get() );
		this.head = this.modelParts.get( "head" );
		this.leftBackFoot = this.modelParts.get( "leftBackFoot" );
		this.leftFrontFoot = this.modelParts.get( "leftFrontFoot" );
		this.rightBackFoot = this.modelParts.get( "rightBackFoot" );
		this.rightFrontFoot = this.modelParts.get( "rightFrontFoot" );
	}

	@Override
	public void setupAnim( Type creeperling, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.modelParts.reset();

		float limbFactor1 = 2.5f * limbSwing * 0.6662f, limbFactor2 = 1.4f * limbSwingAmount;

		this.head.yRot += Math.toRadians( netHeadYaw );
		this.head.xRot += Math.toRadians( headPitch );

		this.leftBackFoot.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightBackFoot.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.rightFrontFoot.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.leftFrontFoot.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
	}

	@Override
	public ModelPart root() {
		return this.modelParts.getRoot();
	}
}


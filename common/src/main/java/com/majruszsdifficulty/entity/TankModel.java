package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.modhelper.LazyResource;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class TankModel< Type extends TankEntity > extends HierarchicalModel< Type > {
	public static LazyResource< ModelDef > MODEL = MajruszsDifficulty.HELPER.load( "tank_model", ModelDef.class, PackType.CLIENT_RESOURCES );
	public final ModelParts modelParts;
	public final ModelPart head, leftLeg, rightLeg, body;

	public TankModel( ModelPart modelPart ) {
		this.modelParts = new ModelParts( modelPart, MODEL.get() );
		this.head = this.modelParts.get( "head" );
		this.leftLeg = this.modelParts.get( "leftLeg" );
		this.rightLeg = this.modelParts.get( "rightLeg" );
		this.body = this.modelParts.get( "body" );
	}

	@Override
	public void setupAnim( Type tank, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.modelParts.reset();

		float limbFactor1 = limbSwing * 0.3333f, limbFactor2 = 0.9f * limbSwingAmount, bodyFactor = 0.2f * limbSwingAmount;

		// head rotation when looking around
		this.head.yRot += Math.toRadians( netHeadYaw );
		this.head.xRot += Math.toRadians( headPitch ) + 0.0873f;

		// leg and body rotation while moving
		this.leftLeg.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightLeg.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.body.zRot += Mth.cos( limbFactor1 ) * bodyFactor;

		tank.getAnimations().forEach( animation->animation.apply( this.modelParts, ageInTicks ) );
	}

	@Override
	public ModelPart root() {
		return this.modelParts.getRoot();
	}
}


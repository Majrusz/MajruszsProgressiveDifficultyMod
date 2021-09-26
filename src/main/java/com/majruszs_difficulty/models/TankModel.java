package com.majruszs_difficulty.models;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Model for new Tank enemy. */
@OnlyIn( Dist.CLIENT )
public class TankModel< Type extends Entity > extends HierarchicalModel< Type > {
	public ModelPart root, body, head, arms, leftArm, leftForearm, rightArm, rightForearm, leftLeg, rightLeg;

	public TankModel( ModelPart modelPart ) {
		this.root = modelPart;
		this.body = this.root.getChild( "body" );
		this.head = this.body.getChild( "head" );
		this.arms = this.body.getChild( "arms" );
		this.leftArm = this.arms.getChild( "leftArm" );
		this.leftForearm = this.leftArm.getChild( "leftForearm" );
		this.rightArm = this.arms.getChild( "rightArm" );
		this.rightForearm = this.rightArm.getChild( "rightForearm" );
		this.leftLeg = this.root.getChild( "leftLeg" );
		this.rightLeg = this.root.getChild( "rightLeg" );
	}

	@Override
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.head.yRot = netHeadYaw * ( ( float )Math.PI / 180f );
		this.head.xRot = headPitch * ( ( float )Math.PI / 180f ) + 0.0873F;
		this.body.yRot = this.head.yRot * 0.4f;

		float limbFactor1 = limbSwing * 0.3333f, limbFactor2 = 0.9f * limbSwingAmount, bodyFactor = 0.2f * limbSwingAmount;
		this.leftLeg.xRot = Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightLeg.xRot = Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.body.zRot = Mth.cos( limbFactor1 ) * bodyFactor;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static LayerDefinition createBodyLayer( CubeDeformation cubeDeformation ) {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild( "body", CubeListBuilder.create()
			.texOffs( 0, 20 )
			.addBox( -8.0F, -22.0F, -5.0F, 16.0F, 12.0F, 8.0F, cubeDeformation )
			.texOffs( 36, 40 )
			.addBox( -1.0F, -23.0F, 1.0F, 2.0F, 22.0F, 2.0F, cubeDeformation.extend( -0.1F ) )
			.texOffs( 0, 40 )
			.addBox( -6.0F, -4.0F, -3.0F, 12.0F, 4.0F, 6.0F, cubeDeformation ), PartPose.offsetAndRotation( 0.0F, 12.0F, 0.0F, 0.1745F, 0.0F, 0.0F ) );

		partDefinition.getChild( "body" )
			.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation ),
				PartPose.offsetAndRotation( 0.0F, -22.0F, -1.0F, 0.0873F, 0.0F, 0.0F )
			);

		partDefinition.getChild( "body" ).addOrReplaceChild( "arms", CubeListBuilder.create(), PartPose.offset( 0.0F, -22.0F, 0.0F ) );

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.addOrReplaceChild( "rightArm",
				CubeListBuilder.create().texOffs( 40, 0 ).addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation ),
				PartPose.offsetAndRotation( -9.5F, 0.0F, -0.5F, 0.0F, 0.0F, 0.1745F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.getChild( "rightArm" )
			.addOrReplaceChild( "rightForearm",
				CubeListBuilder.create().texOffs( 44, 37 ).addBox( -2.5F, -0.5F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation.extend( -0.2F ) ),
				PartPose.offsetAndRotation( 0.0F, 14.5F, 0.0F, -0.7854F, 0.0F, 0.0F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.addOrReplaceChild( "leftArm", CubeListBuilder.create().texOffs( 40, 0 ).addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation ),
				PartPose.offsetAndRotation( 9.5F, 0.0F, -0.5F, 0.0F, 0.0F, -0.1745F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.getChild( "leftArm" )
			.addOrReplaceChild( "leftForearm",
				CubeListBuilder.create().texOffs( 44, 37 ).addBox( -2.5F, -2.0F, -3.5F, 5.0F, 16.0F, 5.0F, cubeDeformation.extend( -0.2F ) ),
				PartPose.offsetAndRotation( 0.0F, 16.0F, 0.0F, -0.7854F, 0.0F, 0.0F )
			);

		partDefinition.addOrReplaceChild( "leftLeg",
			CubeListBuilder.create().texOffs( 48, 21 ).addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation ),
			PartPose.offset( 3.0F, 12.0F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "rightLeg",
			CubeListBuilder.create().texOffs( 48, 21 ).addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation ),
			PartPose.offset( -3.0F, 12.0F, 0.0F )
		);

		return LayerDefinition.create( meshDefinition, 64, 64 );
	}
}

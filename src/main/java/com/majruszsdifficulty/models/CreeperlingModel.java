package com.majruszsdifficulty.models;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Model for new Creeperling enemy. */
@OnlyIn( Dist.CLIENT )
public class CreeperlingModel< Type extends Entity > extends HierarchicalModel< Type > {
	public ModelPart root, body, leftBackFoot, leftFrontFoot, rightFrontFoot, rightBackFoot, head;

	public CreeperlingModel( ModelPart modelPart ) {
		this.root = modelPart;
		this.body = this.root.getChild( "body" );
		this.leftBackFoot = this.root.getChild( "leftBackFoot" );
		this.leftFrontFoot = this.root.getChild( "leftFrontFoot" );
		this.rightFrontFoot = this.root.getChild( "rightFrontFoot" );
		this.rightBackFoot = this.root.getChild( "rightBackFoot" );
		this.head = this.root.getChild( "head" );
	}

	@Override
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.head.yRot = netHeadYaw * ( ( float )Math.PI / 180f );
		this.head.xRot = headPitch * ( ( float )Math.PI / 180f );

		float limbFactor1 = 2.5f * limbSwing * 0.6662f, limbFactor2 = 1.4f * limbSwingAmount;
		this.leftBackFoot.xRot = Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightBackFoot.xRot = Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.rightFrontFoot.xRot = Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.leftFrontFoot.xRot = Mth.cos( limbFactor1 ) * limbFactor2;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static LayerDefinition createBodyLayer( CubeDeformation cubeDeformation ) {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild( "body", CubeListBuilder.create().texOffs( 8, 14 ).addBox( -2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 2.0F, cubeDeformation ), PartPose.offset( 0.0F, 15.0F, 0.0F ) );

		partDefinition.addOrReplaceChild( "leftBackFoot", CubeListBuilder.create().texOffs( 0, 14 ).addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, cubeDeformation ),
			PartPose.offset( 1.0F, 21.0F, -2.0F )
		);

		partDefinition.addOrReplaceChild( "leftFrontFoot", CubeListBuilder.create().texOffs( 0, 14 ).addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, cubeDeformation ),
			PartPose.offset( 1.0F, 21.0F, 2.0F )
		);

		partDefinition.addOrReplaceChild( "rightFrontFoot", CubeListBuilder.create().texOffs( 0, 14 ).addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, cubeDeformation ),
			PartPose.offset( -1.0F, 21.0F, -2.0F )
		);

		partDefinition.addOrReplaceChild( "rightBackFoot", CubeListBuilder.create().texOffs( 0, 14 ).addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, cubeDeformation ),
			PartPose.offset( -1.0F, 21.0F, 2.0F )
		);

		partDefinition.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, cubeDeformation ), PartPose.offset( 0.0F, 15.0F, 0.0F ) );

		return LayerDefinition.create( meshDefinition, 32, 32 );
	}
}

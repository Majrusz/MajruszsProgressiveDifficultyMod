package com.majruszsdifficulty.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class SpiderQueenModel< Type extends Entity > extends HierarchicalModel< Type > {
	final ModelPart root, head, body0, body1, right_front_leg, right_middle_front_leg, right_middle_hind_leg, right_hind_leg, left_front_leg, left_middle_front_leg, left_middle_hind_leg, left_hind_leg;

	public SpiderQueenModel( ModelPart root ) {
		this.root = root;
		this.head = root.getChild( "head" );
		this.body0 = root.getChild( "body0" );
		this.body1 = root.getChild( "body1" );
		this.right_front_leg = root.getChild( "right_front_leg" );
		this.right_middle_front_leg = root.getChild( "right_middle_front_leg" );
		this.right_middle_hind_leg = root.getChild( "right_middle_hind_leg" );
		this.right_hind_leg = root.getChild( "right_hind_leg" );
		this.left_front_leg = root.getChild( "left_front_leg" );
		this.left_middle_front_leg = root.getChild( "left_middle_front_leg" );
		this.left_middle_hind_leg = root.getChild( "left_middle_hind_leg" );
		this.left_hind_leg = root.getChild( "left_hind_leg" );
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild( "head", CubeListBuilder.create()
			.texOffs( 76, 0 )
			.addBox( -6.0F, -3.0F, -12.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation( 0.0F ) )
			.texOffs( 76, 20 )
			.addBox( -4.5F, -3.0F, -10.0F, 9.0F, 1.0F, 8.0F, new CubeDeformation( 0.1F ) ), PartPose.offsetAndRotation( 0.0F, 11.0F, -10.0F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition right_claw_back = head.addOrReplaceChild( "right_claw_back", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -3.0F, -2.0F, -8.0F, 5.0F, 4.0F, 8.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( -3.0F, 1.0F, -8.0F, 0.0F, 0.3927F, -0.3927F ) );

		PartDefinition right_claw_front = right_claw_back.addOrReplaceChild( "right_claw_front", CubeListBuilder.create()
			.texOffs( 0, 60 )
			.addBox( -2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, -8.0F, 0.0F, -1.0472F, 0.0F ) );

		PartDefinition left_claw_back = head.addOrReplaceChild( "left_claw_back", CubeListBuilder.create()
			.texOffs( 0, 48 )
			.addBox( -2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 3.0F, 1.0F, -8.0F, 0.0F, -0.3927F, 0.3927F ) );

		PartDefinition left_claw_front = left_claw_back.addOrReplaceChild( "left_claw_front", CubeListBuilder.create()
			.texOffs( 0, 60 )
			.addBox( -2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( -1.0F, 0.0F, -8.0F, 0.0F, 1.0472F, 0.0F ) );

		PartDefinition body0 = partdefinition.addOrReplaceChild( "body0", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -11.0F, -20.0F, -8.0F, 22.0F, 16.0F, 32.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 24.0F, -2.0F ) );

		PartDefinition body1 = partdefinition.addOrReplaceChild( "body1", CubeListBuilder.create()
			.texOffs( 0, 48 )
			.addBox( -13.0F, -22.0F, -4.0F, 26.0F, 20.0F, 24.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 24.0F, 0.0F ) );

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild( "right_front_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -10.0F, 15.0F, -8.0F, 0.6545F, -0.829F, -0.7854F ) );

		PartDefinition right_front_leg2 = right_front_leg.addOrReplaceChild( "right_front_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition right_front_leg3 = right_front_leg2.addOrReplaceChild( "right_front_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition right_middle_front_leg = partdefinition.addOrReplaceChild( "right_middle_front_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -11.0F, 15.0F, -6.0F, 0.1745F, -0.3491F, -0.5236F ) );

		PartDefinition right_middle_front_leg2 = right_middle_front_leg.addOrReplaceChild( "right_middle_front_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition right_middle_front_leg3 = right_middle_front_leg2.addOrReplaceChild( "right_middle_front_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition right_middle_hind_leg = partdefinition.addOrReplaceChild( "right_middle_hind_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -11.0F, 15.0F, -4.0F, -0.1745F, 0.3491F, -0.5236F ) );

		PartDefinition right_middle_hind_leg2 = right_middle_hind_leg.addOrReplaceChild( "right_middle_hind_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition right_middle_hind_leg3 = right_middle_hind_leg2.addOrReplaceChild( "right_middle_hind_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition right_hind_leg = partdefinition.addOrReplaceChild( "right_hind_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -10.0F, 15.0F, -2.0F, -0.6545F, 0.829F, -0.7854F ) );

		PartDefinition right_hind_leg2 = right_hind_leg.addOrReplaceChild( "right_hind_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition right_hind_leg3 = right_hind_leg2.addOrReplaceChild( "right_hind_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild( "left_front_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 10.0F, 15.0F, -8.0F, -0.6545F, -2.3126F, 0.7854F ) );

		PartDefinition left_front_leg2 = left_front_leg.addOrReplaceChild( "left_front_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition left_front_leg3 = left_front_leg2.addOrReplaceChild( "left_front_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition left_middle_front_leg = partdefinition.addOrReplaceChild( "left_middle_front_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 11.0F, 15.0F, -6.0F, -0.1745F, -2.7925F, 0.5236F ) );

		PartDefinition left_middle_front_leg2 = left_middle_front_leg.addOrReplaceChild( "left_middle_front_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition left_middle_front_leg3 = left_middle_front_leg2.addOrReplaceChild( "left_middle_front_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition left_middle_hind_leg = partdefinition.addOrReplaceChild( "left_middle_hind_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 11.0F, 15.0F, -4.0F, 0.1745F, 2.7925F, 0.5236F ) );

		PartDefinition left_middle_hind_leg2 = left_middle_hind_leg.addOrReplaceChild( "left_middle_hind_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition left_middle_hind_leg3 = left_middle_hind_leg2.addOrReplaceChild( "left_middle_hind_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		PartDefinition left_hind_leg = partdefinition.addOrReplaceChild( "left_hind_leg", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 10.0F, 15.0F, -2.0F, 0.6545F, 2.3126F, 0.7854F ) );

		PartDefinition left_hind_leg2 = left_hind_leg.addOrReplaceChild( "left_hind_leg2", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.3F ) ), PartPose.offsetAndRotation( 0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -1.7453F ) );

		PartDefinition left_hind_leg3 = left_hind_leg2.addOrReplaceChild( "left_hind_leg3", CubeListBuilder.create()
			.texOffs( 0, 12 )
			.addBox( -2.0F, -15.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation( -0.6F ) ), PartPose.offsetAndRotation( 0.0F, -15.0F, 0.0F, 0.0F, 0.0F, -1.0472F ) );

		return LayerDefinition.create( meshdefinition, 128, 96 );
	}

	@Override
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {

	}

	@Override
	public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red,
		float green, float blue, float alpha
	) {
		this.head.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.body0.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.body1.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.right_front_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.right_middle_front_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.right_middle_hind_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.right_hind_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.left_front_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.left_middle_front_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.left_middle_hind_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		this.left_hind_leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
	}

	@Override
	public ModelPart root() {
		return this.head;
	}
}